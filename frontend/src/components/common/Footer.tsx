import './Footer.css';

interface FooterProps {
    onAdminLogin?: () => void;
    onAdminLogout?: () => void;
    isAdmin?: boolean;
}

const Footer = ({ onAdminLogin, onAdminLogout, isAdmin }: FooterProps) => {
    return (
        <footer className="footer">
            <div className="container footer-container">
                <div className="footer-copyright">
                    © {new Date().getFullYear()} Green Alpine Peaks
                </div>
                {!isAdmin ? (
                    <button className="footer-admin-link" onClick={onAdminLogin}>
                        Для партнёров / Админ-панель
                    </button>
                ) : (
                    <button className="footer-admin-link" onClick={onAdminLogout}>
                        Выйти из админ-панели
                    </button>
                )}
            </div>
        </footer>
    );
};

export default Footer;