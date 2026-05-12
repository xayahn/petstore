/**
 * Common test utilities for React Testing Library tests.
 */

import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

/**
 * Wait for async operations to complete.
 * 
 * Usage: await waitForAsync();
 */
export async function waitForAsync() {
  return waitFor(() => {
    expect(true).toBe(true);
  }, { timeout: 100 });
}

/**
 * Get an element by test ID.
 * 
 * @param {string} testId - Test ID of element
 * @returns {HTMLElement}
 */
export function getByTestId(testId) {
  return screen.getByTestId(testId);
}

/**
 * Get multiple elements by test ID.
 * 
 * @param {string} testId - Test ID of elements
 * @returns {HTMLElement[]}
 */
export function getAllByTestId(testId) {
  return screen.getAllByTestId(testId);
}

/**
 * Query elements without throwing if not found.
 * 
 * @param {string} testId - Test ID of element
 * @returns {HTMLElement | null}
 */
export function queryByTestId(testId) {
  return screen.queryByTestId(testId);
}

/**
 * Click an element and wait for async operations.
 * 
 * @param {HTMLElement} element - Element to click
 */
export async function clickAndWait(element) {
  await userEvent.click(element);
  await waitForAsync();
}

/**
 * Type into an input and wait for async operations.
 * 
 * @param {HTMLElement} element - Input element
 * @param {string} text - Text to type
 */
export async function typeAndWait(element, text) {
  await userEvent.type(element, text);
  await waitForAsync();
}

/**
 * Fill a form with provided data.
 * 
 * @param {object} data - Map of label/placeholder to value
 */
export async function fillForm(data) {
  for (const [key, value] of Object.entries(data)) {
    const input = screen.getByPlaceholderText(key) || screen.getByLabelText(key);
    await typeAndWait(input, value);
  }
}

/**
 * Submit a form by finding and clicking submit button.
 * 
 * @returns {Promise}
 */
export async function submitForm() {
  const submitButton = screen.getByRole('button', { name: /submit|save|create|send/i });
  await clickAndWait(submitButton);
}

/**
 * Wait for element to appear in DOM.
 * 
 * @param {string} text - Text content or test ID
 * @param {object} options - waitFor options
 */
export async function waitForElement(text, options = {}) {
  return waitFor(() => screen.getByText(text), options);
}

/**
 * Wait for element to be removed from DOM.
 * 
 * @param {HTMLElement} element - Element to wait for removal
 */
export async function waitForElementToBeRemoved(element) {
  return waitFor(() => {
    if (!element.parentElement) {
      return true;
    }
    throw new Error('Element still in DOM');
  });
}

export { waitFor, screen, userEvent };
