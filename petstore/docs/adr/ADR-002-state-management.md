# ADR-002: React State Management - Context API vs Redux

**Date**: 2026-05-05  
**Status**: ACCEPTED  
**Author**: Petstore Team  
**Deciders**: Frontend Lead, Architecture Team

## Context

The Petstore MVP frontend needs to manage:
1. **Authentication State**: User info, tokens, login/logout
2. **Shopping Cart**: Items, quantities, totals
3. **User Preferences**: Theme, notifications, language
4. **Server Data**: Pets list, orders, seller profile

We need to decide between:
- **Redux**: Powerful, battle-tested state management library
- **React Context API**: Built-in, lightweight alternative

## Decision

Implement **React Context API** with custom hooks for Petstore MVP.

### Architecture

```
AuthContext
├─ isAuthenticated
├─ user { id, email, roles }
├─ tokens { access, refresh }
└─ Methods: login(), logout(), updateUser()

CartContext
├─ items [ { pet_id, name, price, quantity } ]
├─ calculateTotals() → { subtotal, tax, shipping, total }
└─ Methods: addItem(), removeItem(), updateQuantity(), clearCart()

UserContext
├─ profile { address, phone, preferences }
├─ preferences { theme, notifications, language }
└─ Methods: updateProfile(), updatePreferences()
```

### Custom Hooks for Easy Access

```javascript
// Usage
const { isAuthenticated, user, login, logout } = useAuth();
const { items, addItem, removeItem, calculateTotals } = useCart();
const { profile, updateProfile } = useUser();
```

## Rationale

### Redux vs Context API Comparison

| Factor | Redux | Context API |
|--------|-------|-------------|
| Bundle Size | ~4kb (+ middleware) | Included in React |
| Learning Curve | Steep | Gentle |
| Boilerplate | High | Low |
| DevTools | Excellent | Limited |
| Performance | Optimized | Can cause re-renders |
| Scalability | Excellent (100k+) | Good (up to 10k+) |
| Setup Time | 2-3 hours | 30 minutes |

### Why Context API for MVP?

1. **Simplicity**: No boilerplate, easy to understand
2. **Bundle Size**: Reduces initial load (critical for web)
3. **Development Speed**: Faster to set up and modify
4. **Team Knowledge**: Easier to onboard new developers
5. **Sufficient**: MVP doesn't have complex state transitions
6. **Direct Upgrade Path**: Easy to migrate to Redux later if needed

### Why NOT Redux for MVP?

- ✅ Redux shines with: Massive state, complex business logic, time-travel debugging
- ❌ Petstore MVP has: Simple state, few interactions, small team
- ⚠️ Risk: Over-engineering for initial release

## Implementation Details

### AuthContext Example

```javascript
// frontend/src/context/AuthContext.jsx
import React, { createContext, useState, useCallback } from 'react';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [accessToken, setAccessToken] = useState(null);

  const login = useCallback((userData, tokens) => {
    setUser(userData);
    setAccessToken(tokens.access);
    setIsAuthenticated(true);
    localStorage.setItem('user', JSON.stringify(userData));
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setAccessToken(null);
    setIsAuthenticated(false);
    localStorage.removeItem('user');
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, accessToken, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
```

### CartContext with Optimization

```javascript
// Prevent unnecessary re-renders
const [items, setItems] = useState([]);

const addItem = useCallback((pet) => {
  setItems(prev => {
    const existing = prev.find(item => item.pet_id === pet.id);
    if (existing) {
      return prev.map(item =>
        item.pet_id === pet.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      );
    }
    return [...prev, { pet_id: pet.id, name: pet.name, price: pet.price, quantity: 1 }];
  });
}, []);
```

## Alternatives Considered

### 1. Redux
- Pros: Powerful, excellent DevTools, battle-tested at scale
- Cons: Overkill for MVP, high boilerplate, learning curve
- **Decision**: Save for Phase 3+ if complexity grows

### 2. MobX
- Pros: Less boilerplate than Redux, reactive programming
- Cons: Learning curve, smaller community than Redux/Context
- **Decision**: Not chosen - Context API sufficient for MVP

### 3. Zustand
- Pros: Lightweight, simple API, good DevTools
- Cons: Another dependency, smaller ecosystem than Redux
- **Decision**: Could be alternative, but Context chosen for built-in advantage

### 4. Jotai
- Pros: Atomic state management, minimal boilerplate
- Cons: Still a dependency, less mature than Redux
- **Decision**: Not chosen - Context API simpler for MVP

## Architecture Patterns

### Provider Composition

```javascript
// App.jsx
<AuthProvider>
  <CartProvider>
    <UserProvider>
      <BrowserRouter>
        <Header />
        <Routes />
        <Footer />
      </BrowserRouter>
    </UserProvider>
  </CartProvider>
</AuthProvider>
```

### Custom Hooks for Consumption

```javascript
// Components access context via hooks
function ProductCard() {
  const { addItem } = useCart();
  const { isAuthenticated } = useAuth();
  
  return (
    <button onClick={() => addItem(product)}>
      {isAuthenticated ? 'Add to Cart' : 'Login to Buy'}
    </button>
  );
}
```

### LocalStorage Persistence

```javascript
// Persist across page reloads
useEffect(() => {
  const savedAuth = localStorage.getItem('auth');
  if (savedAuth) {
    setAuthState(JSON.parse(savedAuth));
  }
}, []);

// Auto-save on changes
useEffect(() => {
  localStorage.setItem('auth', JSON.stringify({ user, tokens }));
}, [user, tokens]);
```

## Performance Considerations

### Problem: Context Causes Re-renders

```javascript
// ❌ Bad: All consumers re-render when any value changes
const [state, setState] = useState({ user, cart, preferences });
<Context.Provider value={state}>
```

### Solution: Split Contexts by Domain

```javascript
// ✅ Good: Separate contexts for each domain
// AuthContext updates don't trigger CartContext consumers to re-render
<AuthProvider>
  <CartProvider>
    <UserProvider>
      <App />
    </UserProvider>
  </CartProvider>
</AuthProvider>
```

### Further Optimization: useCallback + useMemo

```javascript
// Memoize callbacks to prevent child re-renders
const value = useMemo(() => ({ items, addItem }), [items]);
return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
```

## Migration Path to Redux

If MVP grows beyond context capabilities:

1. **Install Redux + React-Redux**
   ```bash
   npm install redux react-redux @reduxjs/toolkit
   ```

2. **Convert contexts to Redux slices**
   ```javascript
   // authSlice.js
   const authSlice = createSlice({
     name: 'auth',
     initialState: { user: null, tokens: null },
     reducers: { login, logout }
   });
   ```

3. **Replace Context Providers with Redux store**
   ```javascript
   <Provider store={store}>
     <App />
   </Provider>
   ```

4. **Update components to use useSelector/useDispatch**
   ```javascript
   const user = useSelector(state => state.auth.user);
   const dispatch = useDispatch();
   dispatch(login(credentials));
   ```

**Effort**: 1-2 days for full migration if needed

## Monitoring & Debugging

### DevTools Setup

```javascript
// Install browser extension: React DevTools
// Context shows in Component tree
// Can inspect provider values

// Manual debugging
console.log('AuthContext:', useAuth());
console.log('CartContext:', useCart());
```

### State Tracking

```javascript
// Log state changes
const login = useCallback((userData, tokens) => {
  console.debug('AUTH_STATE_CHANGE: LOGIN', { user: userData });
  setUser(userData);
}, []);
```

## Team Considerations

### Developer Experience

| Metric | Context API | Redux |
|--------|-------------|-------|
| Onboarding Time | 1 day | 3 days |
| Context Switching | 5 min | 15 min |
| Debugging | Medium | Easy (DevTools) |
| Code Review | Simple | Complex |

### Documentation

- Simpler internal docs needed for Context API
- Easy to demonstrate to junior developers
- Clear patterns in frontend/src/context/

## Consequences

### Positive ✅
- Faster development (less boilerplate)
- Smaller bundle size
- Easier to understand for new developers
- Built-in to React (no external dependencies)
- Sufficient for MVP state management

### Negative ⚠️
- Limited DevTools (no time-travel debugging)
- Can cause re-renders if not optimized
- Harder to scale if complexity grows
- No built-in middleware support

### Mitigation Strategies
- Implement custom hooks for optimization
- Split contexts by domain (not monolithic)
- Monitor performance with React DevTools Profiler
- Plan Redux migration path

## Success Criteria

- [ ] State persists across page reloads
- [ ] No unnecessary re-renders (< 100ms per update)
- [ ] Easy to add new state (< 30 min per context)
- [ ] Team comfortable with patterns
- [ ] Onboarding docs clear

## References

- [React Context API Documentation](https://react.dev/reference/react/useContext)
- [Redux vs Context API - LogRocket](https://blog.logrocket.com/redux-vs-context-api-real-world-examples/)
- [Context API Performance Optimization](https://react.dev/learn/scaling-up-with-reducer-and-context)

---

**Last Updated**: 2026-05-05
