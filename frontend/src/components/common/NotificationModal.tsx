import { useEffect } from 'react';
import { X, CheckCircle, AlertCircle } from 'lucide-react';
import './NotificationModal.css';

interface NotificationModalProps {
    message: string;
    type: 'success' | 'error';
    onClose: () => void;
    autoCloseDelay?: number;
}

const NotificationModal = ({ message, type, onClose, autoCloseDelay = 3000 }: NotificationModalProps) => {
    useEffect(() => {
        const timer = setTimeout(() => onClose(), autoCloseDelay);
        return () => clearTimeout(timer);
    }, [onClose, autoCloseDelay]);

    return (
        <div className="notification-overlay" onClick={onClose}>
            <div className={`notification-content notification-${type}`} onClick={(e) => e.stopPropagation()}>
                <button className="notification-close" onClick={onClose}>
                    <X size={18} strokeWidth={1.5} />
                </button>

                <div className="notification-icon">
                    {type === 'success' ? (
                        <CheckCircle size={52} strokeWidth={1.5} color="#1E3F20" />
                    ) : (
                        <AlertCircle size={52} strokeWidth={1.5} color="#D32F2F" />
                    )}
                </div>

                <div className="notification-message">{message}</div>

                <div className="notification-progress">
                    <div className="notification-progress-bar"></div>
                </div>
            </div>
        </div>
    );
};

export default NotificationModal;