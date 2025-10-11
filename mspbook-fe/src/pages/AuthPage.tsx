import React, { useState } from 'react';
import { login, register } from '../../services/api';
import { LoginPayload, RegisterPayload } from '../types';

// --- Component InputField (Tái sử dụng) ---
const InputField: React.FC<{ id: string; type: string; placeholder: string; label: string }> = ({ id, type, placeholder, label }) => (
    <div>
        <label htmlFor={id} className="block text-sm font-medium text-gray-700">
            {label}
        </label>
        <input
            type={type}
            id={id}
            name={id}
            className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md text-sm shadow-sm placeholder-gray-400 focus:outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            placeholder={placeholder}
            required
        />
    </div>
);

// --- Component Màn Hình Đăng Nhập / Đăng Ký ---
interface AuthPageProps {
    onLoginSuccess: (token: string) => void;
}

const AuthPage: React.FC<AuthPageProps> = ({ onLoginSuccess }) => {
    const [authMode, setAuthMode] = useState<'login' | 'register'>('login');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const toggleAuthMode = () => {
        setAuthMode(authMode === 'login' ? 'register' : 'login');
        setError(null);
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        const formData = new FormData(e.currentTarget);
        const data = Object.fromEntries(formData.entries()) as any;

        try {
            if (authMode === 'login') {
                const payload: LoginPayload = { username: data.username, password: data.password };
                const result = await login(payload);
                const token = result.token;

                if (token) {
                    onLoginSuccess(token);
                } else {
                    throw new Error('Login failed: No access token received.');
                }
            } else {
                if (data.password !== data.confirmPassword) {
                    throw new Error('Mật khẩu xác nhận không khớp.');
                }
                const registerPayload: RegisterPayload = {
                    username: data.username,
                    password: data.password,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    email: data.email,
                    dob: data.dob,
                };
                await register(registerPayload);
                
                // TỰ ĐỘNG ĐĂNG NHẬP SAU KHI ĐĂNG KÝ THÀNH CÔNG
                const loginPayload: LoginPayload = { username: data.username, password: data.password };
                const loginResult = await login(loginPayload);
                const token = loginResult.token;

                if (token) {
                    onLoginSuccess(token);
                } else {
                    // Nếu tự động đăng nhập thất bại, vẫn thông báo đăng ký thành công và yêu cầu đăng nhập thủ công
                    alert('Đăng ký thành công! Vui lòng đăng nhập.');
                    setAuthMode('login');
                }
            }
        } catch (err: any) {
            const errorMessage = err.message || 'Đã có lỗi xảy ra.';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center font-sans p-4">
            <div className="w-full max-w-md bg-white p-8 rounded-xl shadow-lg">
                <h1 className="text-3xl font-bold text-center text-indigo-600 mb-2">MSPBook</h1>
                <h2 className="text-xl font-semibold text-center text-gray-800 mb-6">
                    {authMode === 'login' ? 'Chào mừng trở lại!' : 'Tạo tài khoản'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {authMode === 'register' && (
                        <>
                            <InputField id="firstName" type="text" placeholder="Văn" label="Tên" />
                            <InputField id="lastName" type="text" placeholder="Nguyễn" label="Họ" />
                        </>
                    )}
                    <InputField id="username" type="text" placeholder="nguyenvan" label="Tên đăng nhập" />
                    {authMode === 'register' && (
                        <>
                            <InputField id="email" type="email" placeholder="you@example.com" label="Địa chỉ Email" />
                            <InputField id="dob" type="date" placeholder="" label="Ngày sinh" />
                        </>
                    )}
                    <InputField id="password" type="password" placeholder="••••••••" label="Mật khẩu" />
                    {authMode === 'register' && (
                        <InputField id="confirmPassword" type="password" placeholder="••••••••" label="Xác nhận Mật khẩu" />
                    )}
                    {error && <p className="text-sm text-red-500 text-center">{error}</p>}
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors disabled:bg-gray-400"
                    >
                        {loading ? 'Đang xử lý...' : (authMode === 'login' ? 'Đăng nhập' : 'Đăng ký')}
                    </button>
                </form>
                <div className="mt-6 text-center">
                    <button onClick={toggleAuthMode} className="text-sm text-indigo-600 hover:underline">
                        {authMode === 'login' ? "Chưa có tài khoản? Đăng ký" : 'Đã có tài khoản? Đăng nhập'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AuthPage;
