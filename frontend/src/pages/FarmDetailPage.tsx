import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Calendar, MapPin, Home, Briefcase, Phone, Mail, ChevronLeft, ChevronRight } from 'lucide-react';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import { farmApi, accommodationTypeApi } from '../services/api';
import type { Farm, AccommodationTypeInterface, Activity } from '../types';
import './FarmDetailPage.css';

const FarmDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [farm, setFarm] = useState<Farm | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [images, setImages] = useState<string[]>([]);
    const [accommodationTypes, setAccommodationTypes] = useState<Map<number, string>>(new Map());

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

    const formatDescription = (description: string) => {
        if (!description) return 'Описание фермы пока не добавлено.';

        let formatted = description.replace(/^[-*•]\s+/gm, '');

        const paragraphs = formatted.split(/\n\s*\n/);

        return paragraphs.map((para, idx) => {
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

    const getAccommodationsList = () => {
        if (!farm?.accommodations || farm.accommodations.length === 0) {
            return <div className="accommodation-empty">Информация о жилье отсутствует</div>;
        }

        return (
            <div className="accommodation-list">
                {farm.accommodations.map((acc, idx) => {
                    const typeName = accommodationTypes.get(acc.typeId) || acc.typeName || acc.type || `Тип ${acc.typeId}`;
                    return (
                        <div key={idx} className="accommodation-item">
                            <span className="accommodation-name">{typeName}</span>
                            <span className="accommodation-dots"></span>
                            <span className="accommodation-price">{acc.price}€ <span className="accommodation-period">/нед</span></span>
                        </div>
                    );
                })}
            </div>
        );
    };

    const getActivitiesList = () => {
        if (!farm?.activities || farm.activities.length === 0) {
            return <div className="activities-empty">Информация о работе отсутствует</div>;
        }

        const activityNames = farm.activities
            .map((activity: Activity) => {
                if (typeof activity === 'string') return activity;
                if (activity && typeof activity === 'object') {
                    if (activity.name && typeof activity.name === 'string') return activity.name;
                    if (activity.id !== undefined) return `Активность ${activity.id}`;
                    return null;
                }
                return null;
            })
            .filter((name): name is string => name !== null && name.trim() !== '');

        if (activityNames.length === 0) {
            return <div className="activities-empty">Информация о работе отсутствует</div>;
        }

        return (
            <ul className="activities-list">
                {activityNames.map((name, idx) => (
                    <li key={idx} className="activities-list-item">
                        <span className="activity-bullet"></span>
                        <span>{name}</span>
                    </li>
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

                {/* ЕДИНЫЙ КОНТЕЙНЕР С ДВУХКОЛОНОЧНОЙ СТРУКТУРОЙ */}
                <div className="farm-card-container">
                    <div className="farm-grid">
                        {/* ЛЕВАЯ КОЛОНКА - 65% */}
                        <div className="farm-left-column">
                            {/* Шапка: название и бейдж статуса */}
                            <div className="farm-header">
                                <h2 className="farm-name">{farm.name}</h2>
                                <div className={`status-badge ${farm.active ? 'status-active' : 'status-inactive'}`}>
                                    <span className="status-dot"></span>
                                    {farm.active ? 'Активна' : 'Не активна'}
                                </div>
                            </div>

                            {/* Блок "О ферме" */}
                            <div className="farm-about">
                                <h3 className="section-title-left">О ферме</h3>
                                <div className="farm-description">
                                    {formatDescription(farm.description || '')}
                                </div>
                            </div>

                            {/* Блок контактов - интегрирован в левую колонку */}
                            <div className="farm-contacts-block">
                                <h3 className="contacts-title">Контакты</h3>
                                <div className="contacts-items">
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
                        </div>

                        {/* ПРАВАЯ КОЛОНКА - 35% (Сайдбар) */}
                        <div className="farm-right-column">
                            {/* Базовые параметры */}
                            <div className="meta-item">
                                <Calendar size={18} className="meta-icon" strokeWidth={1.5} />
                                <span className="meta-label">Год основания:</span>
                                <span className="meta-value">{farm.establishedYear || '—'}</span>
                            </div>

                            <div className="meta-item">
                                <MapPin size={18} className="meta-icon" strokeWidth={1.5} />
                                <span className="meta-label">Регион:</span>
                                <span className="meta-value">{farm.regionName || farm.region || '—'}</span>
                            </div>

                            {/* Разделитель */}
                            <div className="sidebar-divider"></div>

                            {/* Виды жилья */}
                            <div className="sidebar-section">
                                <div className="sidebar-section-header">
                                    <Home size={18} className="section-icon" strokeWidth={1.5} />
                                    <h4 className="sidebar-section-title">Виды жилья</h4>
                                </div>
                                {getAccommodationsList()}
                            </div>

                            {/* Разделитель */}
                            <div className="sidebar-divider"></div>

                            {/* Работа и развлечения */}
                            <div className="sidebar-section">
                                <div className="sidebar-section-header">
                                    <Briefcase size={18} className="section-icon" strokeWidth={1.5} />
                                    <h4 className="sidebar-section-title">Работа и развлечения</h4>
                                </div>
                                {getActivitiesList()}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default FarmDetailPage;