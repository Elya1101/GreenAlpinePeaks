import { useState } from 'react';
import './LoginPage.css';

interface LoginPageProps {
    onLogin: (email: string, password: string) => void;
    onCancel: () => void;
    error?: string;
}

const LoginPage = ({ onLogin, onCancel, error }: LoginPageProps) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [fieldErrors, setFieldErrors] = useState<{ email?: string; password?: string }>({});

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const errors: { email?: string; password?: string } = {};

        if (!email.trim()) errors.email = 'Введите email';
        if (!password) errors.password = 'Введите пароль';

        if (Object.keys(errors).length > 0) {
            setFieldErrors(errors);
            return;
        }

        setFieldErrors({});
        onLogin(email, password);
    };

    return (
        <div className="login-page">
            <div className="login-container">
                <div className="login-form">
                    <button className="login-close" onClick={onCancel}>✕</button>
                    <h2 className="login-title">Авторизация</h2>
                    <p className="login-subtitle">Вход в админ-панель</p>

                    <form onSubmit={handleSubmit}>
                        <div className="login-field">
                            <input
                                type="email"
                                className={`login-input ${fieldErrors.email ? 'login-input-error' : ''}`}
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                            {fieldErrors.email && <span className="login-error">{fieldErrors.email}</span>}
                        </div>

                        <div className="login-field">
                            <div className="password-wrapper">
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    className={`login-input ${fieldErrors.password ? 'login-input-error' : ''}`}
                                    placeholder="Пароль"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? '🙈' : '👁️'}
                                </button>
                            </div>
                            {fieldErrors.password && <span className="login-error">{fieldErrors.password}</span>}
                            {error && <span className="login-error server-error">{error}</span>}
                        </div>

                        <button type="submit" className="login-button">
                            Войти
                        </button>
                        <button type="button" className="login-cancel-button" onClick={onCancel}>
                            Отмена
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;