import React, { useState, useEffect } from 'react';
import HomePage from './src/pages/HomePage';
import AuthPage from './src/pages/AuthPage';

// --- Component APP Chính (Bộ định tuyến) ---
const App: React.FC = () => {
  const [token, setToken] = useState<string | null>(null);

  // Khi ứng dụng khởi động, kiểm tra xem có token nào được lưu trong localStorage không.
  useEffect(() => {
    const storedToken = localStorage.getItem('msp_token');
    if (storedToken) {
      setToken(storedToken);
    }
  }, []);

  // Hàm được gọi bởi AuthPage khi đăng nhập thành công.
  const handleLoginSuccess = (newToken: string) => {
    localStorage.setItem('msp_token', newToken);
    setToken(newToken);
  };

  // Hàm được gọi bởi HomePage khi người dùng đăng xuất.
  const handleLogout = () => {
    localStorage.removeItem('msp_token');
    setToken(null);
  };

  // ĐIỀU HƯỚNG ĐƠN GIẢN:
  // Nếu có token, hiển thị trang chủ (HomePage).
  // Nếu không có token, hiển thị màn hình đăng nhập (AuthPage).
  return token ? <HomePage token={token} onLogout={handleLogout} /> : <AuthPage onLoginSuccess={handleLoginSuccess} />;
};

export default App;
