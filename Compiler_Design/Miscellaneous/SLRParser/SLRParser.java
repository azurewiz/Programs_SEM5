package SLRParser;

import java.util.*;

public class SLRParser {
  static class Production {
    String lhs; // Left-hand side of the production
    List<String> rhs; // Right-hand side of the production

    Production(String lhs, String rhs) {
      this.lhs = lhs;
      this.rhs = Arrays.asList(rhs.split(" "));
    }

    @Override
    public String toString() {
      return lhs + " -> " + String.join(" ", rhs);
    }
  }

  static class Item {
    String lhs;
    List<String> rhs;
    int dotPosition;

    Item(String lhs, List<String> rhs, int dotPosition) {
      this.lhs = lhs;
      this.rhs = rhs;
      this.dotPosition = dotPosition;
    }

    boolean isComplete() {
      return dotPosition == rhs.size();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      Item item = (Item) obj;
      return dotPosition == item.dotPosition && Objects.equals(lhs, item.lhs) && Objects.equals(rhs, item.rhs);
    }

    @Override
    public int hashCode() {
      return Objects.hash(lhs, rhs, dotPosition);
    }

    @Override
    public String toString() {
      return lhs + " -> " + String.join(" ", rhs.subList(0, dotPosition)) + " • "
          + String.join(" ", rhs.subList(dotPosition, rhs.size()));
    }
  }

  static Set<String> terminals = new HashSet<>();
  static Set<String> nonTerminals = new HashSet<>();
  static List<Production> productions = new ArrayList<>();
  static Map<String, Set<String>> firstSet = new HashMap<>();
  static Map<String, Set<String>> followSet = new HashMap<>();

  static Map<Integer, Map<String, String>> actionTable = new HashMap<>();
  static Map<Integer, Map<String, Integer>> gotoTable = new HashMap<>();
  static List<Set<Item>> canonicalCollection = new ArrayList<>();

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Input CFG productions
    System.out.println("Enter number of productions: ");
    int n = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter productions (example: E -> E + T): ");
    for (int i = 0; i < n; i++) {
      String productionInput = scanner.nextLine().trim();
      String[] parts = productionInput.split("->");
      String lhs = parts[0].trim();
      String rhs = parts[1].trim();
      productions.add(new Production(lhs, rhs));
      nonTerminals.add(lhs);
      for (String symbol : rhs.split(" ")) {
        if (!symbol.equals("") && !Character.isUpperCase(symbol.charAt(0))) {
          terminals.add(symbol);
        }
      }
    }
    scanner.close();

    // Add augmented production (S' -> S) where S is the start symbol
    String startSymbol = productions.get(0).lhs;
    productions.add(0, new Production("S'", startSymbol));
    nonTerminals.add("S'");

    // Compute FIRST and FOLLOW sets
    computeFirstSets();
    computeFollowSets();

    // Compute Canonical Collection of LR(0) Items
    computeCanonicalCollection();

    // Build Action and Goto tables
    buildParsingTable();

    // Display parsing table
    displayParsingTable();
  }

  static void computeFirstSets() {
    for (Production production : productions) {
      firstSet.putIfAbsent(production.lhs, new HashSet<>());
    }

    boolean changed;
    do {
      changed = false;
      for (Production production : productions) {
        String lhs = production.lhs;
        for (String symbol : production.rhs) {
          if (terminals.contains(symbol)) {
            if (firstSet.get(lhs).add(symbol)) {
              changed = true;
            }
            break;
          } else if (nonTerminals.contains(symbol)) {
            Set<String> firstOfSymbol = firstSet.get(symbol);
            int sizeBefore = firstSet.get(lhs).size();
            firstSet.get(lhs).addAll(firstOfSymbol);
            firstSet.get(lhs).remove("ε");
            if (firstSet.get(lhs).size() > sizeBefore) {
              changed = true;
            }
            if (!firstOfSymbol.contains("ε"))
              break;
          }
        }
      }
    } while (changed);
  }

  static void computeFollowSets() {
    for (String nonTerminal : nonTerminals) {
      followSet.put(nonTerminal, new HashSet<>());
    }
    followSet.get(productions.get(0).lhs).add("$");

    boolean changed;
    do {
      changed = false;
      for (Production production : productions) {
        String lhs = production.lhs;
        for (int i = 0; i < production.rhs.size(); i++) {
          String symbol = production.rhs.get(i);
          if (nonTerminals.contains(symbol)) {
            Set<String> followOfSymbol = followSet.get(symbol);
            if (i + 1 < production.rhs.size()) {
              String nextSymbol = production.rhs.get(i + 1);
              if (terminals.contains(nextSymbol)) {
                if (followOfSymbol.add(nextSymbol)) {
                  changed = true;
                }
              } else if (nonTerminals.contains(nextSymbol)) {
                Set<String> firstOfNext = firstSet.get(nextSymbol);
                int sizeBefore = followOfSymbol.size();
                followOfSymbol.addAll(firstOfNext);
                followOfSymbol.remove("ε");
                if (followOfSymbol.size() > sizeBefore) {
                  changed = true;
                }
              }
            } else {
              if (followOfSymbol.addAll(followSet.get(lhs))) {
                changed = true;
              }
            }
          }
        }
      }
    } while (changed);
  }

  static void computeCanonicalCollection() {
    Set<Item> startItemSet = closure(
        new HashSet<>(Collections.singletonList(new Item("S'", Collections.singletonList(productions.get(0).lhs), 0))));
    canonicalCollection.add(startItemSet);

    boolean changed;
    do {
      changed = false;
      for (int i = 0; i < canonicalCollection.size(); i++) {
        Set<Item> itemSet = canonicalCollection.get(i);
        for (String symbol : getSymbols()) {
          Set<Item> nextItemSet = gotoOperation(itemSet, symbol);
          if (!nextItemSet.isEmpty() && !canonicalCollection.contains(nextItemSet)) {
            canonicalCollection.add(nextItemSet);
            changed = true;
          }
        }
      }
    } while (changed);
  }

  static Set<String> getSymbols() {
    Set<String> symbols = new HashSet<>(terminals);
    symbols.addAll(nonTerminals);
    return symbols;
  }

  static Set<Item> closure(Set<Item> items) {
    Set<Item> closureSet = new HashSet<>(items);

    boolean changed;
    do {
      changed = false;
      Set<Item> newItems = new HashSet<>(closureSet);
      for (Item item : closureSet) {
        if (item.dotPosition < item.rhs.size()) {
          String symbol = item.rhs.get(item.dotPosition);
          if (nonTerminals.contains(symbol)) {
            for (Production production : productions) {
              if (production.lhs.equals(symbol)) {
                Item newItem = new Item(production.lhs, production.rhs, 0);
                if (newItems.add(newItem)) {
                  changed = true;
                }
              }
            }
          }
        }
      }
      closureSet = newItems;
    } while (changed);

    return closureSet;
  }

  static Set<Item> gotoOperation(Set<Item> items, String symbol) {
    Set<Item> nextItems = new HashSet<>();
    for (Item item : items) {
      if (item.dotPosition < item.rhs.size() && item.rhs.get(item.dotPosition).equals(symbol)) {
        nextItems.add(new Item(item.lhs, item.rhs, item.dotPosition + 1));
      }
    }
    return closure(nextItems);
  }

  static void buildParsingTable() {
    for (int i = 0; i < canonicalCollection.size(); i++) {
      Set<Item> itemSet = canonicalCollection.get(i);
      actionTable.putIfAbsent(i, new HashMap<>());
      gotoTable.putIfAbsent(i, new HashMap<>());

      for (Item item : itemSet) {
        if (!item.isComplete()) {
          String symbol = item.rhs.get(item.dotPosition);
          if (terminals.contains(symbol)) {
            Set<Item> nextItemSet = gotoOperation(itemSet, symbol);
            int nextState = canonicalCollection.indexOf(nextItemSet);
            actionTable.get(i).put(symbol, "s" + nextState);
          } else if (nonTerminals.contains(symbol)) {
            Set<Item> nextItemSet = gotoOperation(itemSet, symbol);
            int nextState = canonicalCollection.indexOf(nextItemSet);
            gotoTable.get(i).put(symbol, nextState);
          }
        } else {
          if (item.lhs.equals("S'")) {
            actionTable.get(i).put("$", "accept");
          } else {
            int productionIndex = productions.indexOf(new Production(item.lhs, String.join(" ", item.rhs)));
            for (String terminal : followSet.get(item.lhs)) {
              actionTable.get(i).put(terminal, "r" + productionIndex);
            }
          }
        }
      }
    }
  }

  static void displayParsingTable() {
    System.out.println("\nSLR Parsing Table:");
    System.out.println("State | Action Table | Goto Table");
    System.out.println("---------------------------------");

    for (int state : actionTable.keySet()) {
      System.out.print(state + "     | ");
      System.out.print(actionTable.get(state) + "  | ");
      System.out.println(gotoTable.get(state));
    }
  }
}
