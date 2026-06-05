// src/pages/FarmDetailPage.tsx
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, Home, Briefcase, Phone, Mail, ChevronLeft, ChevronRight } from 'lucide-react';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import { farmApi, accommodationTypeApi } from '../services/api';
import type { Farm, AccommodationTypeInterface } from '../types';
import './FarmDetailPage.css';

const FarmDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [farm, setFarm] = useState<Farm | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [images, setImages] = useState<string[]>([]);
    const [accommodationTypes, setAccommodationTypes] = useState<Map<number, string>>(new Map());

    // Загружаем типы жилья для маппинга ID -> название
    useEffect(() => {
        const loadAccommodationTypes = async () => {
            try {
                const types = await accommodationTypeApi.getAllTypes();
                const typesMap = new Map<number, string>();
                types.forEach((type: AccommodationTypeInterface) => {
                    typesMap.set(type.id, type.name);
                });
                setAccommodationTypes(typesMap);
            } catch (err) {
                console.error('Ошибка загрузки типов жилья:', err);
            }
        };
        loadAccommodationTypes();
    }, []);

    useEffect(() => {
        const loadFarm = async () => {
            if (!id) return;

            try {
                const farmId = parseInt(id);
                const data = await farmApi.getFarmById(farmId);
                setFarm(data);

                try {
                    const farmImages = await farmApi.getFarmImages(farmId);
                    const imageUrls = farmImages.map(img =>
                        img.url.startsWith('http')
                            ? img.url
                            : `http://localhost:8080${img.url}`
                    );
                    setImages(imageUrls);
                } catch (imageError) {
                    console.error('Ошибка загрузки изображений:', imageError);
                    setImages([]);
                }
            } catch (err) {
                console.error('Ошибка загрузки фермы:', err);
            } finally {
                setLoading(false);
            }
        };

        loadFarm();
    }, [id]);

    const nextImage = () => {
        if (images.length === 0) return;
        setCurrentImageIndex(prev => (prev + 1) % images.length);
    };

    const prevImage = () => {
        if (images.length === 0) return;
        setCurrentImageIndex(prev => (prev - 1 + images.length) % images.length);
    };

    // Функция для красивого форматирования описания
    const formatDescription = (description: string) => {
        if (!description) return 'Описание фермы пока не добавлено.';

        // Убираем символы маркированных списков в начале строк
        let formatted = description.replace(/^[-*•]\s+/gm, '');

        // Разбиваем на абзацы по двойным переносам строк
        const paragraphs = formatted.split(/\n\s*\n/);

        return paragraphs.map((para, idx) => {
            // Если в абзаце есть переносы строк, оборачиваем каждую строку в span
            if (para.includes('\n')) {
                const lines = para.split('\n');
                return (
                    <p key={idx} className="farm-description-paragraph">
                        {lines.map((line, lineIdx) => (
                            <span key={lineIdx}>
                                {line}
                                {lineIdx < lines.length - 1 && <br />}
                            </span>
                        ))}
                    </p>
                );
            }
            return <p key={idx} className="farm-description-paragraph">{para}</p>;
        });
    };

    // Форматируем список проживания - КАЖДЫЙ ТИП С НОВОЙ СТРОКИ
    const getAccommodationsList = () => {
        if (!farm?.accommodations || farm.accommodations.length === 0) {
            return <div className="accommodation-empty">Информация о жилье отсутствует</div>;
        }

        return (
            <ul className="accommodation-list">
                {farm.accommodations.map((acc, idx) => {
                    const typeName = accommodationTypes.get(acc.typeId) || acc.typeName || acc.type || `Тип ${acc.typeId}`;
                    return (
                        <li key={idx} className="accommodation-list-item">
                            {typeName} — {acc.price}€ / неделя
                        </li>
                    );
                })}
            </ul>
        );
    };

    // Форматируем список активностей
    const getActivitiesList = () => {
        if (!farm?.activities || farm.activities.length === 0) {
            return 'Информация о работе отсутствует';
        }

        const activityNames = farm.activities
            .map(activity => {
                if (typeof activity === 'string') return activity;
                if (activity && typeof activity === 'object') {
                    if (activity.name && typeof activity.name === 'string') return activity.name;
                    if (activity.title && typeof activity.title === 'string') return activity.title;
                    if (activity.activityName && typeof activity.activityName === 'string') return activity.activityName;
                    return null;
                }
                return null;
            })
            .filter((name): name is string => name !== null && name.trim() !== '');

        if (activityNames.length === 0) {
            return 'Информация о работе отсутствует';
        }

        return (
            <ul className="activities-list">
                {activityNames.map((name, idx) => (
                    <li key={idx} className="activities-list-item">{name}</li>
                ))}
            </ul>
        );
    };

    if (loading) {
        return (
            <div>
                <Header />
                <div className="loading-spinner">Загрузка фермы...</div>
                <Footer />
            </div>
        );
    }

    if (!farm) {
        return (
            <div>
                <Header />
                <div className="error-message">Ферма не найдена</div>
                <Footer />
            </div>
        );
    }

    return (
        <div>
            <Header />

            <div className="farm-hero">
                <div className="container">
                    <h1 className="farm-hero-title">{farm.name}</h1>
                </div>
            </div>

            <div className="container">
                <button className="back-button" onClick={() => navigate('/')}>
                    ← На главную
                </button>

                <div className="farm-gallery">
                    {images.length > 0 ? (
                        <>
                            <div className="gallery-container">
                                <button className="gallery-nav prev" onClick={prevImage} aria-label="Предыдущее фото">
                                    <ChevronLeft size={28} strokeWidth={2} />
                                </button>
                                <img src={images[currentImageIndex]} alt={farm.name} className="gallery-image" />
                                <button className="gallery-nav next" onClick={nextImage} aria-label="Следующее фото">
                                    <ChevronRight size={28} strokeWidth={2} />
                                </button>
                            </div>
                            <div className="gallery-thumbnails">
                                {images.map((img, idx) => (
                                    <img
                                        key={idx}
                                        src={img}
                                        alt={`Фото ${idx + 1}`}
                                        className={`thumbnail ${idx === currentImageIndex ? 'active' : ''}`}
                                        onClick={() => setCurrentImageIndex(idx)}
                                    />
                                ))}
                            </div>
                        </>
                    ) : (
                        <div className="gallery-container">
                            <div className="gallery-placeholder">
                                <Home size={64} strokeWidth={1} color="#CBD5E0" />
                                <p>Нет фотографий</p>
                            </div>
                        </div>
                    )}
                </div>

                <div className="farm-section">
                    <div className="farm-section-grid">
                        <div className="farm-section-left">
                            <h2 className="section-title">О ферме</h2>
                            <div className={`status-badge ${farm.active ? 'status-active-badge' : 'status-inactive-badge'}`}>
                                {farm.active ? '● Активна' : '○ Не активна'}
                            </div>
                            <div className="farm-description">
                                {formatDescription(farm.description || '')}
                            </div>
                        </div>

                        <div className="farm-section-right">
                            <div className="fact-item">
                                <Calendar size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Год основания:</span>
                                <span className="fact-value">{farm.establishedYear || '—'}</span>
                            </div>

                            <div className="fact-item">
                                <MapPin size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Регион:</span>
                                <span className="fact-value">{farm.regionName || farm.region || '—'}</span>
                            </div>

                            <div className="fact-item">
                                <Home size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Виды жилья:</span>
                                <div className="fact-value">{getAccommodationsList()}</div>
                            </div>

                            <div className="fact-item">
                                <Briefcase size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Работа и развлечения:</span>
                                <div className="fact-value">{getActivitiesList()}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="farm-contacts">
                    <h2 className="section-title">Контактные данные фермы</h2>
                    <div className="contact-item">
                        <Phone size={18} className="contact-icon" strokeWidth={1.5} />
                        <span className="contact-text">{farm.phone || 'Телефон не указан'}</span>
                    </div>
                    <div className="contact-item">
                        <Mail size={18} className="contact-icon" strokeWidth={1.5} />
                        <span className="contact-text">{farm.email || 'Email не указан'}</span>
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default FarmDetailPage;