import { useEffect } from 'react';
import './NotificationModal.css';

interface NotificationModalProps {
    message: string;
    type: 'success' | 'error';
    onClose: () => void;
    autoCloseDelay?: number;
}

const NotificationModal = ({ message, type, onClose, autoCloseDelay = 3000 }: NotificationModalProps) => {
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose();
        }, autoCloseDelay);

        return () => clearTimeout(timer);
    }, [onClose, autoCloseDelay]);

    return (
        <div className="notification-overlay" onClick={onClose}>
            <div className={`notification-content notification-${type}`} onClick={(e) => e.stopPropagation()}>
                <div className="notification-icon">
                    {type === 'success' ? '✅' : '❌'}
                </div>
                <div className="notification-message">{message}</div>
                <button className="notification-close" onClick={onClose}>✕</button>
                <div className="notification-progress">
                    <div className="notification-progress-bar"></div>
                </div>
            </div>
        </div>
    );
};

export default NotificationModal;