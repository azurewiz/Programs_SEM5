// Function to fetch JSON data and validate user login
async function fetchUsersAndValidate(event) {
  event.preventDefault(); // Prevent the default form submission

  // Get form field values
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
    // Fetch the users.json file
    const response = await fetch("./data/users.json");

    // Check if the response is successful
    if (!response.ok) {
      throw new Error("Failed to load user data");
    }

    // Parse the JSON data
    const data = await response.json();
    const users = data.users;

    // Check if the user exists
    const user = users.find(
      (u) => u.email === email && u.password === password,
    );

    if (user) {
      alert("Login successful!");
      console.log(`User ID: ${user.id}, Email: ${user.email}`);
    } else {
      showErrorModal("Invalid email or password!");
    }
  } catch (error) {
    showErrorModal("An error occurred while fetching user data.");
    console.error(error);
  }
}

// Function to show the error modal with a message
function showErrorModal(message) {
  const modalErrorMessage = document.getElementById("modalErrorMessage");
  modalErrorMessage.textContent = message;

  const errorModal = new bootstrap.Modal(document.getElementById("errorModal"));
  errorModal.show();
}

// Attach event listener to the form submit
document
  .getElementById("loginForm")
  .addEventListener("submit", fetchUsersAndValidate);
