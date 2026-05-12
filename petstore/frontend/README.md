# Petstore Frontend - React 18 + Vite 4

React single-page application for the Petstore peer-to-peer pet marketplace. Built with Vite 4+ for fast development and optimized production builds.

## 🚀 Quick Start

### Prerequisites
- **Node.js**: 18 LTS or 20+
- **npm**: 9+ or yarn/pnpm

### Development

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# The app will be available at http://localhost:5173
```

### Build for Production

```bash
# Create optimized production build
npm run build

# Preview production build locally
npm run preview
```

## 📁 Project Structure

```
frontend/
├── package.json                    # npm dependencies and scripts
├── vite.config.js                  # Vite configuration (dev server, proxy, aliases)
├── jest.config.js                  # Jest testing configuration
├── .eslintrc.js                    # ESLint code quality rules
├── .prettierrc                     # Prettier code formatting
├── index.html                      # HTML entry point
├── src/
│   ├── main.jsx                    # React entry point
│   ├── App.jsx                     # Root component with routing
│   ├── App.css                     # Root styles
│   ├── pages/                      # Route pages (coming soon)
│   │   ├── HomePage.jsx
│   │   ├── LoginPage.jsx
│   │   └── ...
│   ├── components/                 # Reusable UI components
│   │   ├── Header.jsx
│   │   ├── Footer.jsx
│   │   └── ...
│   ├── context/                    # React Context for state management
│   │   ├── AuthContext.jsx         # Authentication state
│   │   ├── CartContext.jsx         # Shopping cart state
│   │   └── UserContext.jsx         # User preferences state
│   ├── hooks/                      # Custom React hooks
│   │   ├── useAuth.js              # Auth context hook
│   │   ├── useCart.js              # Cart context hook
│   │   └── useFetch.js             # API fetch hook
│   ├── services/                   # API client services
│   │   └── api.js                  # Axios instance with interceptors
│   ├── styles/                     # Global styles
│   │   ├── index.css               # CSS variables and resets
│   │   └── ...
│   └── __tests__/                  # Jest test files
│       ├── setup.js
│       └── ...
└── Dockerfile                      # Production build
Dockerfile.dev                    # Development server
```

## 🛠️ Development

### Available Scripts

```bash
# Development server with HMR
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run unit tests
npm test

# Run tests in watch mode
npm test -- --watch

# Generate coverage report
npm test -- --coverage

# Run E2E tests
npm run e2e

# Run E2E tests with UI
npm run e2e:ui

# Lint code
npm run lint

# Fix linting issues
npm run lint:fix

# Format code
npm run format
```

## 📦 State Management

### Context API Architecture

```
App
  ├── AuthContext (user, tokens, login/logout)
  ├── CartContext (items, addItem, removeItem)
  └── UserContext (preferences, profile)
```

### Usage

```jsx
import { useAuth } from '../hooks/useAuth';

function MyComponent() {
  const { isAuthenticated, user, logout } = useAuth();
  
  return (
    <>
      {isAuthenticated && <p>Welcome, {user.first_name}</p>}
      <button onClick={logout}>Logout</button>
    </>
  );
}
```

## 🌐 API Integration

### Axios Configuration

The `src/services/api.js` provides an Axios instance with:
- Automatic JWT token injection
- Request/response interceptors
- Automatic token refresh on 401
- Centralized error handling

### Making API Calls

```jsx
import api from '../services/api';
import { useFetch } from '../hooks/useFetch';

// Option 1: Using useFetch hook (with loading/error states)
function PetBrowser() {
  const { data: pets, loading, error } = useFetch('/api/pets');
  
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  
  return <div>{pets?.map(pet => <div key={pet.id}>{pet.name}</div>)}</div>;
}

// Option 2: Direct API calls
async function handleLogin(email, password) {
  try {
    const response = await api.post('/api/auth/login', { email, password });
    const { user, tokens } = response.data.data;
    login(user, tokens);
  } catch (error) {
    console.error('Login failed:', error.response?.data?.error);
  }
}
```

## 🧪 Testing

### Unit Tests (Jest + React Testing Library)

```bash
# Run all tests
npm test

# Run specific file
npm test HomePage.test.jsx

# Watch mode
npm test -- --watch

# Coverage
npm test -- --coverage
```

Example test:

```jsx
import { render, screen } from '@testing-library/react';
import Header from './Header';

describe('Header Component', () => {
  it('renders navigation links', () => {
    render(<Header />);
    expect(screen.getByText('Home')).toBeInTheDocument();
  });
});
```

### E2E Tests (Playwright)

```bash
# Run all tests
npm run e2e

# Run specific test
npm run e2e -- login

# UI mode (watch tests)
npm run e2e:ui

# Debug mode
npm run e2e:debug
```

Example test:

```javascript
import { test, expect } from '@playwright/test';

test('user can browse pets', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.click('text=Browse Pets');
  await expect(page).toHaveURL('http://localhost:5173/pets');
});
```

## 🎨 Styling

### CSS Strategy

- **Global styles**: `src/styles/index.css` (CSS variables, resets)
- **Component styles**: Colocated `.css` files (e.g., `Header.css` next to `Header.jsx`)
- **CSS Variables**: Defined in `:root` for theming

### CSS Variables

```css
:root {
  --primary-color: #2c3e50;
  --secondary-color: #3498db;
  --success-color: #27ae60;
  --danger-color: #e74c3c;
  --light-gray: #ecf0f1;
  /* ... more variables */
}
```

## 🚀 Deployment

### Docker Build

```bash
# Development image (Vite dev server)
docker build -f Dockerfile.dev -t petstore-frontend:dev .

# Production image (Nginx serving static files)
docker build -f Dockerfile -t petstore-frontend:prod .
```

### Production Build Optimization

- Automatic code splitting for faster loads
- Tree-shaking unused code
- CSS minification
- JavaScript minification and obfuscation
- Asset compression

Build output in `dist/`:
```
dist/
├── index.html
├── assets/
│   ├── index-abc123.js    # Main JS bundle
│   ├── vendor-def456.js   # Vendor dependencies
│   └── style-ghi789.css   # Styles
└── ...
```

## ⚙️ Configuration

### Vite Config (vite.config.js)

- Dev server: `http://localhost:5173`
- API proxy: `/api` → `http://localhost:8080`
- Aliases: `@` for `src/`, `@components` for `src/components/`, etc.

### Environment Variables

Create `.env.local` for development:

```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_STRIPE_PUBLIC_KEY=pk_test_xxx
```

Environment variables in code:

```jsx
const apiBase = import.meta.env.VITE_API_BASE_URL;
const stripeKey = import.meta.env.VITE_STRIPE_PUBLIC_KEY;
```

## 🔐 Authentication Flow

1. User submits login credentials
2. Backend returns `access_token` + `refresh_token`
3. Frontend stores tokens in localStorage
4. All API requests include `Authorization: Bearer <token>` header
5. On 401 response, automatically refresh token
6. If refresh fails, redirect to login

## 📊 Performance

**Vite advantages**:
- Sub-100ms rebuild times during development
- Native ES modules for dev (no bundling)
- Fast HMR (Hot Module Replacement)
- Optimized production build with code splitting

**Target metrics**:
- Page load: <2 seconds
- API response: <500ms
- Lighthouse score: 90+

## 🐛 Debugging

### React DevTools
1. Install React Developer Tools browser extension
2. Open DevTools → Components tab
3. Inspect component state and props

### Vite Dev Server
```bash
# Verbose logging
VITE_DEBUG=true npm run dev
```

## 🔍 Code Quality

### Linting (ESLint)

```bash
npm run lint           # Check issues
npm run lint:fix       # Auto-fix issues
```

### Formatting (Prettier)

```bash
npm run format         # Format all files
```

### Pre-commit Hooks (Optional)

Install husky for auto-linting before commit:
```bash
npm install husky --save-dev
npx husky install
```

## 🌍 Browser Support

- Chrome/Edge: Latest versions
- Firefox: Latest versions  
- Safari: 14+
- IE: Not supported (ES2020 target)

## 📚 Resources

- [React Documentation](https://react.dev)
- [Vite Guide](https://vitejs.dev/guide/)
- [Jest Testing](https://jestjs.io)
- [React Testing Library](https://testing-library.com/react)
- [Playwright E2E Testing](https://playwright.dev)

## 🆘 Troubleshooting

**Port 5173 already in use**
```bash
# Change port in vite.config.js or use:
npm run dev -- --port 3001
```

**API requests failing**
```bash
# Check VITE_API_BASE_URL is correct
# Verify backend is running on :8080
# Check browser console for CORS errors
```

**Tests failing**
```bash
# Clear Jest cache
npm test -- --clearCache

# Run in verbose mode
npm test -- --verbose
```

---

**Last Updated**: 2026-05-05  
**React Version**: 18.2+  
**Vite Version**: 5.0+  
**Node**: 18 LTS / 20+
