import csv
import re

FILE_PATH = "../mnt/data/india.svg"

with open(FILE_PATH, mode="r", encoding="utf-8") as file:
    svg_content = file.read()

# Use regex to extract the <path> elements from the SVG
path_elements = re.findall(r"<path[^>]+>", svg_content)

print(svg_content)

# Extract id, d, and title attributes from each path element
extracted_data = []
for path in path_elements:
    id_match = re.search(r'id="([^"]+)"', path)
    d_match = re.search(r'd="([^"]+)"', path)
    title_match = re.search(r'title="([^"]+)"', path)

    if id_match and d_match and title_match:
        extracted_data.append(
            {
                "id": id_match.group(1),
                "d": d_match.group(1),
                "title": title_match.group(1),
            }
        )

# Define the file path for saving the CSV
CSV_FILE_PATH = "../mnt/data/india_map_transcribed.csv"

# Writing the extracted data to CSV
with open(CSV_FILE_PATH, mode="w", newline="", encoding="utf-8") as file:
    writer = csv.DictWriter(file, fieldnames=["id", "d", "title"])
    writer.writeheader()
    writer.writerows(extracted_data)

    for row in extracted_data:
        row["d"] = str(row["d"])
        writer.writerow(row)

XML_FILE_PATH = "../mnt/data/svg_content.xml"
with open(XML_FILE_PATH, mode="w", newline="", encoding="utf-8") as file:
    file.write(svg_content)
