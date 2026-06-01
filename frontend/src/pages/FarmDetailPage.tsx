import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import { farmApi } from '../services/api';
import type { Farm } from '../types';
import './FarmDetailPage.css';

// Словарь для перевода типов проживания на человеческий язык
const accommodationTypeMap: { [key: string]: string } = {
    AGRITOURISM_ROOM: 'Агротуристическая комната',
    DAIRY_GUEST_ROOM: 'Гостевая комната на ферме',
    ALPINE_HUT: 'Альпийская хижина',
    APARTMENT: 'Апартаменты',
    TENT: 'Палатка',
    HOUSE: 'Дом',
    LODGE: 'Лодж'
};

const FarmDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [farm, setFarm] = useState<Farm | null>(null);
    const [loading, setLoading] = useState(true);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [images, setImages] = useState<string[]>([]);

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

    // Форматируем список проживания для отображения
    const accommodationsList = farm.accommodations?.map(acc =>
        `${accommodationTypeMap[acc.type] || acc.type} — ${acc.price}€ / неделя`
    ).join('; ') || 'Информация о жилье отсутствует';

    // Форматируем список активностей
    const activitiesList = farm.activities?.map(act => act.name).join(', ') || 'Информация о работе отсутствует';

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

                {/* Галерея */}
                <div className="farm-gallery">
                    {images.length > 0 ? (
                        <>
                            <div className="gallery-container">
                                <button className="gallery-nav prev" onClick={prevImage}>⟨</button>
                                <img src={images[currentImageIndex]} alt={farm.name} className="gallery-image" />
                                <button className="gallery-nav next" onClick={nextImage}>⟩</button>
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
                            <img src="https://via.placeholder.com/1200x800?text=Нет+фото" alt="Нет фотографий" className="gallery-image" />
                        </div>
                    )}
                </div>

                {/* Блок "О ферме" - без телефона и email */}
                <div className="farm-section">
                    <div className="farm-section-grid">
                        <div className="farm-section-left">
                            <h2 className="section-title">О ферме</h2>
                            <div className={`status-badge ${farm.active ? 'status-active-badge' : 'status-inactive-badge'}`}>
                                {farm.active ? '● Активна' : '○ Не активна'}
                            </div>
                            <p className="farm-description">
                                {farm.description || 'Описание фермы пока не добавлено. Это место, где традиции альпийского фермерства сочетаются с гостеприимством и заботой о природе.'}
                            </p>
                        </div>

                        <div className="farm-section-right">
                            <div className="fact-item">
                                <span className="fact-icon">📅</span>
                                <span className="fact-label">Год основания:</span>
                                <span className="fact-value">{farm.establishedYear || '—'}</span>
                            </div>
                            <div className="fact-item">
                                <span className="fact-icon">📍</span>
                                <span className="fact-label">Регион:</span>
                                <span className="fact-value">{farm.region}</span>
                            </div>
                            <div className="fact-item">
                                <span className="fact-icon">🏠</span>
                                <span className="fact-label">Виды жилья:</span>
                                <span className="fact-value">{accommodationsList}</span>
                            </div>
                            <div className="fact-item">
                                <span className="fact-icon">🎯</span>
                                <span className="fact-label">Работа и развлечения:</span>
                                <span className="fact-value">{activitiesList}</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Контактные данные фермы - теперь показываем реальные данные фермы */}
                <div className="farm-contacts">
                    <h2 className="section-title">Контактные данные фермы</h2>
                    <div className="contact-item">
                        <span className="contact-icon">📞</span>
                        <span className="contact-text">{farm.phone || 'Телефон не указан'}</span>
                    </div>
                    <div className="contact-item">
                        <span className="contact-icon">✉️</span>
                        <span className="contact-text">{farm.email || 'Email не указан'}</span>
                    </div>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default FarmDetailPage;