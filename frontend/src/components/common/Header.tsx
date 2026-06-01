import { useState } from 'react';
import './Header.css';

interface HeaderProps {
    isAdmin?: boolean;
    viewMode?: 'admin' | 'user';
    onViewModeChange?: (mode: 'admin' | 'user') => void;
}

const Header = ({ isAdmin = false, viewMode = 'user', onViewModeChange }: HeaderProps) => {
    const [editingField, setEditingField] = useState<string | null>(null);
    const [editValue, setEditValue] = useState('');

    const [workingHours, setWorkingHours] = useState([
        { days: 'Пн - Пт', hours: '10:00 - 19:00' },
        { days: 'Сб', hours: '10:00 - 18:00' }
    ]);

    const [phones, setPhones] = useState([
        { number: '+375 (29) 369-45-89', label: 'для туристов' },
        { number: '+375 (44) 700-92-65', label: 'для агентов' }
    ]);

    const handleEditHours = (index: number) => {
        setEditingField(`hours-${index}`);
        setEditValue(workingHours[index].hours);
    };

    const handleEditPhone = (index: number) => {
        setEditingField(`phone-${index}`);
        setEditValue(phones[index].number);
    };

    const saveHours = (index: number) => {
        const newHours = [...workingHours];
        newHours[index].hours = editValue;
        setWorkingHours(newHours);
        setEditingField(null);
    };

    const savePhone = (index: number) => {
        const newPhones = [...phones];
        newPhones[index].number = editValue;
        setPhones(newPhones);
        setEditingField(null);
    };

    return (
        <header className="header">
            <div className="container header-container">
                <div className="header-logo">
                    <span className="logo-text">GreenAlpinePeaks</span>
                    {isAdmin && viewMode === 'admin' && <button className="edit-icon" title="Редактировать логотип">✏️</button>}
                </div>

                <div className="header-hours">
                    <span className="header-icon">🕒</span>
                    <div className="hours-text">
                        {workingHours.map((item, idx) => (
                            <div key={idx} className="hours-row">
                                {editingField === `hours-${idx}` ? (
                                    <>
                                        <span>{item.days}: </span>
                                        <input
                                            type="text"
                                            value={editValue}
                                            onChange={(e) => setEditValue(e.target.value)}
                                            onBlur={() => saveHours(idx)}
                                            onKeyPress={(e) => e.key === 'Enter' && saveHours(idx)}
                                            className="edit-input-small"
                                            autoFocus
                                        />
                                    </>
                                ) : (
                                    <span>{item.days}: {item.hours}</span>
                                )}
                                {isAdmin && viewMode === 'admin' && (
                                    <button
                                        className="inline-edit-icon-small"
                                        onClick={() => handleEditHours(idx)}
                                    >
                                        ✏️
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </div>

                <div className="header-phones">
                    <span className="header-icon">📞</span>
                    <div className="phones-text">
                        {phones.map((phone, idx) => (
                            <div key={idx} className="phone-row">
                                {editingField === `phone-${idx}` ? (
                                    <>
                                        <input
                                            type="text"
                                            value={editValue}
                                            onChange={(e) => setEditValue(e.target.value)}
                                            onBlur={() => savePhone(idx)}
                                            onKeyPress={(e) => e.key === 'Enter' && savePhone(idx)}
                                            className="edit-input-small"
                                            autoFocus
                                        />
                                        <span className="phone-label"> ({phone.label})</span>
                                    </>
                                ) : (
                                    <>
                                        <strong>{phone.number}</strong>
                                        <span className="phone-label"> ({phone.label})</span>
                                    </>
                                )}
                                {isAdmin && viewMode === 'admin' && (
                                    <button
                                        className="inline-edit-icon-small"
                                        onClick={() => handleEditPhone(idx)}
                                    >
                                        ✏️
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </div>

                {/* Переключатель режимов (только для админа) */}
                {isAdmin && (
                    <div className="view-mode-switcher">
                        <button
                            className={`mode-btn ${viewMode === 'user' ? 'active' : ''}`}
                            onClick={() => onViewModeChange?.('user')}
                        >
                            👁️ Пользователь
                        </button>
                        <button
                            className={`mode-btn ${viewMode === 'admin' ? 'active' : ''}`}
                            onClick={() => onViewModeChange?.('admin')}
                        >
                            ✏️ Админ
                        </button>
                    </div>
                )}
            </div>
        </header>
    );
};

export default Header;