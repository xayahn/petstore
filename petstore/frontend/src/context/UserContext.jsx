import React, { createContext, useState, useCallback } from 'react';

/**
 * User context for managing user profile and preferences.
 */
export const UserContext = createContext();

export function UserProvider({ children }) {
  const [profile, setProfile] = useState(null);
  const [preferences, setPreferences] = useState({
    theme: localStorage.getItem('theme') || 'light',
    notifications: localStorage.getItem('notifications') !== 'false',
  });
  const [loading, setLoading] = useState(false);

  const updateProfile = useCallback((profileData) => {
    setProfile(profileData);
  }, []);

  const updatePreferences = useCallback((newPreferences) => {
    setPreferences((prev) => {
      const updated = { ...prev, ...newPreferences };
      if (newPreferences.theme) {
        localStorage.setItem('theme', newPreferences.theme);
      }
      if (typeof newPreferences.notifications === 'boolean') {
        localStorage.setItem('notifications', newPreferences.notifications);
      }
      return updated;
    });
  }, []);

  const value = {
    profile,
    preferences,
    loading,
    updateProfile,
    updatePreferences,
  };

  return (
    <UserContext.Provider value={value}>
      {children}
    </UserContext.Provider>
  );
}
