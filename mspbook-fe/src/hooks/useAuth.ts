import { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  scope: string;
  sub: string;
  // Thêm các trường khác nếu có trong token của bạn
}

export const useAuth = () => {
  const [auth, setAuth] = useState<{ token: string | null; roles: string[]; userId: string | null }>({ token: null, roles: [], userId: null });

  useEffect(() => {
    const token = localStorage.getItem('msp_token');
    if (token) {
      try {
        const decoded = jwtDecode<DecodedToken>(token);
        const roles = decoded.scope ? decoded.scope.split(' ') : [];
        setAuth({ token, roles, userId: decoded.sub });
      } catch (error) {
        console.error("Invalid token:", error);
        setAuth({ token: null, roles: [], userId: null });
      }
    }
  }, []);

  const isAdmin = auth.roles.includes('ROLE_ADMIN');

  return { ...auth, isAdmin };
};
