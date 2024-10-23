document.addEventListener("DOMContentLoaded", function () {
  let stateData = {};

  // Load state data from JSON file
  fetch("./scripts/state_details.json")
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Network error: ${response.statusText}`);
      }
      return response.json();
    })
    .then((data) => {
      console.log("State data successfully loaded:", data);
      stateData = data;
    })
    .catch((error) => {
      console.error("Failed to load state data:", error);
    });

  const states = document.querySelectorAll(".state");
  const infoBox = document.getElementById("info-box");

  // When mouse hovers over a state
  states.forEach((state) => {
    state.addEventListener("mouseover", function () {
      const stateId = state.getAttribute("id");
      console.log(stateId);
      if (stateData[stateId]) {
        const state = stateData[stateId];
        console.log(state.name);

        infoBox.innerHTML = `
        <h4>${state.name}</h4>
        <p><strong>Population:</strong> ${state.population}</p>
        <p><strong>Capital:</strong> ${state.capital}</p>
        <p><strong>Area:</strong> ${state.area}</p>
        <p>${state.description}</p>
      `;
      }
      // infoBox.innerHTML = `<strong>${stateName}</strong>`;
      infoBox.classList.add("active");
    });

    // Move the info box with the mouse
    state.addEventListener("mousemove", function (e) {
      infoBox.style.top = `${e.pageY - 50}px`;
      infoBox.style.left = `${e.pageX + 25}px`;
    });

    // Hide info box when mouse leaves
    state.addEventListener("mouseout", function () {
      infoBox.classList.remove("active");
    });
  });
});
