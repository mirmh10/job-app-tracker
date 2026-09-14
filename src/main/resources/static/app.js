"use strict";

const API_URL = "/api/applications";
const form = document.querySelector("#application-form");
const fields = {
  company: document.querySelector("#company"),
  role: document.querySelector("#role"),
  dateApplied: document.querySelector("#dateApplied"),
  url: document.querySelector("#url"),
  status: document.querySelector("#status"),
  notes: document.querySelector("#notes"),
};
const body = document.querySelector("#applications-body");
const filter = document.querySelector("#status-filter");
const count = document.querySelector("#application-count");
const emptyState = document.querySelector("#empty-state");
const formTitle = document.querySelector("#form-title");
const submitButton = document.querySelector("#submit-button");
const cancelButton = document.querySelector("#cancel-button");
const feedback = document.querySelector("#feedback");

let applications = [];
let editingId = null;

function showError(message) {
  feedback.textContent = message;
  feedback.hidden = false;
}

function clearError() {
  feedback.textContent = "";
  feedback.hidden = true;
}

async function request(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error(`HTTP ${response.status}`);
  return response.status === 204 ? null : response.json();
}

async function refreshApplications() {
  try {
    applications = await request(API_URL);
    clearError();
    render();
  } catch {
    showError("Could not load applications. Check that the Java server is running.");
  }
}

function resetForm() {
  form.reset();
  editingId = null;
  formTitle.textContent = "Add an application";
  submitButton.textContent = "Add application";
  cancelButton.hidden = true;
}

function makeCell(row, text, className) {
  const cell = document.createElement("td");
  if (className) cell.className = className;
  cell.textContent = text;
  row.append(cell);
  return cell;
}

function render() {
  const visible = applications
    .filter((item) => filter.value === "All" || item.status === filter.value)
    .sort((a, b) => b.dateApplied.localeCompare(a.dateApplied) || b.id - a.id);

  body.replaceChildren();
  count.textContent = `Showing ${visible.length} of ${applications.length} applications`;
  emptyState.hidden = visible.length > 0;
  emptyState.querySelector("strong").textContent = applications.length
    ? "No matches for this status."
    : "No applications yet.";
  emptyState.lastChild.textContent = applications.length
    ? " Choose another status to see your applications."
    : " Add your first one above!";

  for (const item of visible) {
    const row = document.createElement("tr");
    const companyCell = document.createElement("td");
    const company = document.createElement("span");
    company.className = "company";
    company.textContent = item.company;
    const role = document.createElement("span");
    role.className = "role";
    role.textContent = item.role;
    companyCell.append(company, role);
    row.append(companyCell);

    makeCell(row, item.dateApplied);
    const statusCell = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = `badge badge-${item.status.toLowerCase()}`;
    badge.textContent = item.status;
    statusCell.append(badge);
    row.append(statusCell);

    const linkCell = document.createElement("td");
    if (item.url) {
      const link = document.createElement("a");
      link.className = "job-link";
      link.href = item.url;
      link.target = "_blank";
      link.rel = "noopener noreferrer";
      link.textContent = "Open posting";
      linkCell.append(link);
    } else {
      linkCell.textContent = "—";
    }
    row.append(linkCell);
    makeCell(row, item.notes || "—", "notes");

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "row-actions";
    const edit = document.createElement("button");
    edit.type = "button";
    edit.className = "button button-secondary";
    edit.textContent = "Edit";
    edit.setAttribute("aria-label", `Edit ${item.company} application`);
    edit.addEventListener("click", () => startEdit(item.id));
    const remove = document.createElement("button");
    remove.type = "button";
    remove.className = "button button-danger";
    remove.textContent = "Delete";
    remove.setAttribute("aria-label", `Delete ${item.company} application`);
    remove.addEventListener("click", () => deleteApplication(item.id));
    actions.append(edit, remove);
    actionsCell.append(actions);
    row.append(actionsCell);
    body.append(row);
  }
}

function startEdit(id) {
  const item = applications.find((application) => application.id === id);
  if (!item) return;
  editingId = id;
  for (const key of Object.keys(fields)) fields[key].value = item[key];
  formTitle.textContent = "Edit application";
  submitButton.textContent = "Save changes";
  cancelButton.hidden = false;
  form.scrollIntoView({ behavior: "smooth", block: "start" });
  fields.company.focus({ preventScroll: true });
}

async function deleteApplication(id) {
  const item = applications.find((application) => application.id === id);
  if (!item || !window.confirm(`Delete the application for ${item.company}?`)) return;
  try {
    await request(`${API_URL}/${id}`, { method: "DELETE" });
    if (editingId === id) resetForm();
    await refreshApplications();
  } catch {
    showError("Could not delete this application. Please try again.");
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!form.reportValidity()) return;

  const values = Object.fromEntries(
    Object.entries(fields).map(([key, field]) => [key, field.value.trim()])
  );
  if (!values.company || !values.role) {
    const field = !values.company ? fields.company : fields.role;
    field.setCustomValidity("This field cannot be blank.");
    field.reportValidity();
    return;
  }
  if (values.url) {
    try {
      const url = new URL(values.url);
      if (!["http:", "https:"].includes(url.protocol)) throw new Error("Invalid link");
    } catch {
      fields.url.setCustomValidity("Enter an http or https link.");
      fields.url.reportValidity();
      return;
    }
  }

  submitButton.disabled = true;
  try {
    await request(editingId === null ? API_URL : `${API_URL}/${editingId}`, {
      method: editingId === null ? "POST" : "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(values),
    });
    resetForm();
    await refreshApplications();
  } catch {
    showError("Could not save this application. Check the fields and try again.");
  } finally {
    submitButton.disabled = false;
  }
});

for (const field of Object.values(fields)) {
  field.addEventListener("input", () => field.setCustomValidity(""));
  field.addEventListener("change", () => field.setCustomValidity(""));
}
cancelButton.addEventListener("click", resetForm);
filter.addEventListener("change", render);
refreshApplications();
