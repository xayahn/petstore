import React from 'react';
import { Link } from 'react-router-dom';
import './Footer.css';

/**
 * Footer component with company info and links.
 * 
 * Contains:
 * - Copyright and company info
 * - Quick links
 * - Support links
 * - Social media links (future)
 */
export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section">
          <h4>About Petstore</h4>
          <p>
            A peer-to-peer marketplace connecting pet lovers. Buy and sell pets safely with our verified seller community.
          </p>
        </div>

        <div className="footer-section">
          <h4>Quick Links</h4>
          <ul>
            <li>
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/pets">Browse Pets</Link>
            </li>
            <li>
              <Link to="/seller">Become a Seller</Link>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h4>Support</h4>
          <ul>
            <li>
              <a href="mailto:support@petstore.com">Contact Us</a>
            </li>
            <li>
              <a href="#privacy">Privacy Policy</a>
            </li>
            <li>
              <a href="#terms">Terms of Service</a>
            </li>
          </ul>
        </div>

        <div className="footer-section">
          <h4>Follow Us</h4>
          <div className="social-links">
            <a href="#facebook">Facebook</a>
            <a href="#twitter">Twitter</a>
            <a href="#instagram">Instagram</a>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <p>
          &copy; {currentYear} Petstore. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
