import { useEffect, useState, useRef, useCallback } from 'react';
import { useParams, useNavigate, useBeforeUnload } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import { farmApi } from '../services/api';
import type { Farm, Activity, Accommodation, Image } from '../types';
import './AdminFarmPage.css';

const accommodationTypeMap: { [key: string]: string } = {
    'AGRITOURISM_ROOM': 'Агротуристическая комната',
    'DAIRY_GUEST_ROOM': 'Гостевая комната на ферме',
    'ALPINE_HUT': 'Альпийская хижина',
    'APARTMENT': 'Апартаменты',
    'TENT': 'Палатка',
    'HOUSE': 'Дом',
    'LODGE': 'Лодж'
};

interface ActivityWithStatus {
    id?: number;
    name: string;
    status: 'existing' | 'new' | 'deleted';
}

interface AccommodationWithStatus {
    id?: number;
    type: string;
    price: number;
    status: 'existing' | 'new' | 'deleted';
}

interface ImageData {
    id?: number;
    file?: File;
    previewUrl: string;
    isMain: boolean;
    status: 'existing' | 'new' | 'deleted';
}

const AdminFarmPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const isNewFarm = id === 'new';
    const fileInputRef = useRef<HTMLInputElement>(null);

    const [farm, setFarm] = useState<Farm | null>(null);
    const [originalFarm, setOriginalFarm] = useState<Farm | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [hasChanges, setHasChanges] = useState(false);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
    const [showDeleteFarmConfirm, setShowDeleteFarmConfirm] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Редактируемые поля
    const [editName, setEditName] = useState('');
    const [editDescription, setEditDescription] = useState('');
    const [editEstablishedYear, setEditEstablishedYear] = useState('');
    const [editRegion, setEditRegion] = useState('');
    const [editEmail, setEditEmail] = useState('');
    const [editPhone, setEditPhone] = useState('');

    // Активности с отслеживанием статуса
    const [activities, setActivities] = useState<ActivityWithStatus[]>([]);
    const [newActivityName, setNewActivityName] = useState('');

    // Проживание с отслеживанием статуса
    const [accommodations, setAccommodations] = useState<AccommodationWithStatus[]>([]);
    const [newAccommodationType, setNewAccommodationType] = useState('');
    const [newAccommodationPrice, setNewAccommodationPrice] = useState(0);

    // Изображения
    const [images, setImages] = useState<ImageData[]>([]);

    // Предупреждение о несохраненных изменениях
    useBeforeUnload(
        useCallback(() => {
            if (hasChanges) {
                return 'У вас есть несохраненные изменения. Вы уверены, что хотите покинуть страницу?';
            }
        }, [hasChanges])
    );

    const loadImages = async (farmId: number) => {
        try {
            const data = await farmApi.getFarmImages(farmId);
            setImages(data.map(img => ({
                id: img.id,
                previewUrl: img.url.startsWith('http') ? img.url : `http://localhost:8080${img.url}`,
                isMain: img.isMain,
                status: 'existing'
            })));
        } catch (err) {
            console.error('Ошибка загрузки изображений:', err);
            setError('Не удалось загрузить изображения');
            setImages([]);
        }
    };

    const loadFarmData = async (farmId: number) => {
        try {
            const data = await farmApi.getFarmById(farmId);
            setFarm(data);
            setOriginalFarm(JSON.parse(JSON.stringify(data)));

            setEditName(data.name);
            setEditDescription(data.description || '');
            setEditEstablishedYear(String(data.establishedYear || ''));
            setEditRegion(data.region);
            setEditPhone(data.phone || '');
            setEditEmail(data.email || '');

            // Загружаем активности с ID
            setActivities(data.activities.map(a => ({
                id: a.id,
                name: a.name,
                status: 'existing'
            })));

            // Загружаем проживание с ID
            setAccommodations(data.accommodations.map(a => ({
                id: a.id,
                type: a.type,
                price: a.price,
                status: 'existing'
            })));

            await loadImages(farmId);
        } catch (err) {
            console.error('Ошибка загрузки фермы:', err);
            setError('Не удалось загрузить данные фермы');
        }
    };

    useEffect(() => {
        const loadFarm = async () => {
            if (isNewFarm) {
                const emptyFarm: Farm = {
                    id: 0,
                    name: 'Новая ферма',
                    region: 'Швейцария',
                    active: true,
                    description: '',
                    establishedYear: new Date().getFullYear(),
                    phone: '',
                    email: '',
                    activities: [],
                    accommodations: []
                };
                setFarm(emptyFarm);
                setOriginalFarm(JSON.parse(JSON.stringify(emptyFarm)));
                setEditName(emptyFarm.name);
                setEditDescription('');
                setEditEstablishedYear(String(emptyFarm.establishedYear));
                setEditRegion(emptyFarm.region);
                setEditPhone('');
                setEditEmail('');
                setActivities([]);
                setAccommodations([]);
                setImages([]);
                setLoading(false);
                return;
            }

            if (!id) return;
            await loadFarmData(parseInt(id));
            setLoading(false);
        };

        loadFarm();
    }, [id, isNewFarm]);

    // Проверка изменений - ИСПРАВЛЕННАЯ ВЕРСИЯ
    useEffect(() => {
        if (!farm) return;

        const hasNameChange = editName !== (originalFarm?.name || '');
        const hasDescChange = editDescription !== (originalFarm?.description || '');
        const hasYearChange = editEstablishedYear !== String(originalFarm?.establishedYear || '');
        const hasRegionChange = editRegion !== (originalFarm?.region || '');
        const hasPhoneChange = editPhone !== (originalFarm?.phone || '');
        const hasEmailChange = editEmail !== (originalFarm?.email || '');

        const hasActivityChanges = activities.some(a => a.status !== 'existing');
        const hasAccommodationChanges = accommodations.some(a => a.status !== 'existing');
        const hasImageChanges = images.some(img => img.status !== 'existing');

        setHasChanges(hasNameChange || hasDescChange || hasYearChange ||
            hasRegionChange || hasPhoneChange || hasEmailChange ||
            hasActivityChanges || hasAccommodationChanges || hasImageChanges);
    }, [editName, editDescription, editEstablishedYear, editRegion,
        editPhone, editEmail, activities, accommodations, images, farm, originalFarm]);

    // Сохранение активностей
    const saveActivities = async (farmId: number) => {
        const toDelete = activities.filter(a => a.status === 'deleted' && a.id);
        const toAdd = activities.filter(a => a.status === 'new');

        // Удаляем удаленные активности
        for (const activity of toDelete) {
            if (activity.id) {
                await farmApi.removeActivityFromFarm(farmId, activity.id);
            }
        }

        // Добавляем новые активности
        for (const activity of toAdd) {
            await farmApi.addActivityToFarm(farmId, activity.name);
        }
    };

    // Сохранение проживания
    const saveAccommodations = async (farmId: number) => {
        const toDelete = accommodations.filter(a => a.status === 'deleted' && a.id);
        const toAdd = accommodations.filter(a => a.status === 'new');

        // Удаляем удаленное проживание
        for (const accommodation of toDelete) {
            if (accommodation.id) {
                await farmApi.deleteAccommodation(accommodation.id);
            }
        }

        // Добавляем новое проживание
        for (const accommodation of toAdd) {
            await farmApi.addAccommodationToFarm(farmId, accommodation.type, accommodation.price);
        }
    };

    // Сохранение изображений
    const saveImages = async (farmId: number) => {
        // Удаляем помеченные изображения
        const toDelete = images.filter(img => img.status === 'deleted' && img.id);
        for (const img of toDelete) {
            if (img.id) {
                await farmApi.deleteImage(farmId, img.id);
            }
        }

        // Загружаем новые изображения
        const toUpload = images.filter(img => img.status === 'new');
        for (const img of toUpload) {
            if (img.file) {
                await farmApi.uploadImage(farmId, img.file, img.isMain);
            }
        }

        // Обновляем главное фото если изменилось
        const mainImageChanged = images.some(img => img.status === 'existing' &&
            img.isMain !== (img.id === images.find(i => i.isMain)?.id));
        if (mainImageChanged) {
            const mainImage = images.find(img => img.isMain && img.id);
            if (mainImage?.id) {
                await farmApi.setMainImage(farmId, mainImage.id);
            }
        }

        await loadImages(farmId);
    };

    const handleSave = async () => {
        setSaving(true);
        setError(null);

        try {
            const updateData: any = {
                name: editName,
                active: farm?.active ?? true,
            };

            // Добавляем только непустые поля
            if (editDescription && editDescription.trim()) {
                updateData.description = editDescription;
            }
            if (editEstablishedYear && parseInt(editEstablishedYear) > 0) {
                updateData.establishedYear = parseInt(editEstablishedYear);
            }
            if (editRegion && editRegion.trim()) {
                updateData.region = editRegion;
            }
            if (editPhone && editPhone.trim()) {
                updateData.phone = editPhone;
            }
            if (editEmail && editEmail.trim()) {
                updateData.email = editEmail;
            }

            console.log('Saving farm data:', updateData); // Для отладки

            let farmId: number;

            if (isNewFarm) {
                const newFarm = await farmApi.createFarm(updateData);
                farmId = newFarm.id;
                await saveActivities(farmId);
                await saveAccommodations(farmId);
                await saveImages(farmId);
                alert('Ферма успешно создана!');
                navigate(`/admin/farms/${farmId}`);
            } else if (farm) {
                await farmApi.updateFarm(farm.id, updateData);
                await saveActivities(farm.id);
                await saveAccommodations(farm.id);
                await saveImages(farm.id);
                alert('Изменения успешно сохранены!');
                await loadFarmData(farm.id);
                setHasChanges(false);
            }
        } catch (err: any) {
            console.error('Ошибка сохранения:', err);
            console.error('Response data:', err.response?.data);
            setError(err.response?.data?.message || err.response?.data?.error || 'Не удалось сохранить изменения');
        } finally {
            setSaving(false);
        }
    };

    const handleCancel = () => {
        if (hasChanges && !confirm('У вас есть несохраненные изменения. Вы уверены, что хотите выйти?')) {
            return;
        }
        navigate('/');
    };

    const handleDeleteFarm = async () => {
        if (!farm) return;
        try {
            await farmApi.deleteFarm(farm.id);
            alert('Ферма успешно удалена');
            navigate('/');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Не удалось удалить ферму');
        }
    };

    // Управление активностями
    const addActivity = () => {
        if (newActivityName.trim()) {
            setActivities([...activities, {
                name: newActivityName.trim(),
                status: 'new'
            }]);
            setNewActivityName('');
        }
    };

    const removeActivity = (index: number) => {
        const activity = activities[index];
        if (activity.id) {
            // Помечаем как удаленную, если она уже есть на сервере
            setActivities(activities.map((a, i) =>
                i === index ? { ...a, status: 'deleted' } : a
            ));
        } else {
            // Просто удаляем, если новая
            setActivities(activities.filter((_, i) => i !== index));
        }
    };

    // Управление проживанием
    const addAccommodation = () => {
        if (newAccommodationType && newAccommodationPrice > 0) {
            setAccommodations([...accommodations, {
                type: newAccommodationType,
                price: newAccommodationPrice,
                status: 'new'
            }]);
            setNewAccommodationType('');
            setNewAccommodationPrice(0);
        }
    };

    const removeAccommodation = (index: number) => {
        const accommodation = accommodations[index];
        if (accommodation.id) {
            // Помечаем как удаленную
            setAccommodations(accommodations.map((a, i) =>
                i === index ? { ...a, status: 'deleted' } : a
            ));
        } else {
            // Просто удаляем
            setAccommodations(accommodations.filter((_, i) => i !== index));
        }
    };

    // Управление изображениями
    const addImage = () => {
        fileInputRef.current?.click();
    };

    const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (event) => {
            const visibleCount = images.filter(img => img.status !== 'deleted').length;
            setImages(prev => [...prev, {
                file,
                previewUrl: event.target?.result as string,
                isMain: visibleCount === 0,
                status: 'new'
            }]);
        };
        reader.readAsDataURL(file);

        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const deleteImage = () => {
        const visibleImages = images.filter(img => img.status !== 'deleted');
        const imageToDelete = visibleImages[currentImageIndex];
        if (!imageToDelete) return;

        setImages(prev => prev.map(img => {
            if (img === imageToDelete) {
                if (img.id) {
                    return { ...img, status: 'deleted' };
                }
                return null;
            }
            return img;
        }).filter(Boolean) as ImageData[]);

        const remainingVisible = images.filter(img => img !== imageToDelete && img.status !== 'deleted');
        if (currentImageIndex >= remainingVisible.length) {
            setCurrentImageIndex(Math.max(0, remainingVisible.length - 1));
        }

        setShowDeleteConfirm(false);
    };

    const setAsMainImage = (index: number) => {
        const visibleImages = images.filter(img => img.status !== 'deleted');
        const targetImage = visibleImages[index];
        if (!targetImage) return;

        setImages(prev => prev.map(img => ({
            ...img,
            isMain: img === targetImage
        })));
    };

    const toggleActiveStatus = async () => {
        if (!farm) return;
        try {
            await farmApi.updateFarm(farm.id, { active: !farm.active });
            const updatedFarm = { ...farm, active: !farm.active };
            setFarm(updatedFarm);
            if (originalFarm) {
                setOriginalFarm({ ...originalFarm, active: !farm.active });
            }
        } catch (err) {
            console.error('Ошибка изменения статуса:', err);
            setError('Не удалось изменить статус фермы');
        }
    };

    const getVisibleImages = () => images.filter(img => img.status !== 'deleted');

    const visibleImages = getVisibleImages();

    if (loading) {
        return (
            <div>
                <Header isAdmin={true} />
                <div className="loading-spinner">Загрузка...</div>
                <Footer />
            </div>
        );
    }

    if (error && !farm) {
        return (
            <div>
                <Header isAdmin={true} />
                <div className="error-message">{error}</div>
                <Footer />
            </div>
        );
    }

    return (
        <div>
            <Header isAdmin={true} />

            <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                onChange={handleFileSelect}
            />

            <div className="farm-hero admin-hero">
                <div className="container">
                    <h1 className="farm-hero-title">{editName}</h1>
                </div>
            </div>

            <div className="container">
                <div className="admin-nav-buttons">
                    <button className="back-button" onClick={() => navigate('/')}>
                        ← На главную
                    </button>
                    <button className="delete-farm-button" onClick={() => setShowDeleteFarmConfirm(true)}>
                        🗑️ Удалить ферму
                    </button>
                </div>

                {error && <div className="error-message">{error}</div>}

                {/* Галерея */}
                <div className="farm-gallery admin-gallery">
                    {visibleImages.length > 0 ? (
                        <>
                            <div className="gallery-container">
                                <button className="gallery-nav prev" onClick={() => {
                                    setCurrentImageIndex(prev => (prev - 1 + visibleImages.length) % visibleImages.length);
                                }}>⟨</button>

                                <div className="gallery-image-wrapper">
                                    <img src={visibleImages[currentImageIndex]?.previewUrl} alt="Ферма" className="gallery-image" />
                                    {visibleImages[currentImageIndex]?.isMain && (
                                        <div className="main-image-badge">Главное фото</div>
                                    )}
                                    <button className="gallery-delete-btn" onClick={() => setShowDeleteConfirm(true)}>
                                        🗑️
                                    </button>
                                    {!visibleImages[currentImageIndex]?.isMain && (
                                        <button className="gallery-set-main-btn" onClick={() => setAsMainImage(currentImageIndex)}>
                                            ⭐
                                        </button>
                                    )}
                                </div>

                                <button className="gallery-nav next" onClick={() => {
                                    setCurrentImageIndex(prev => (prev + 1) % visibleImages.length);
                                }}>⟩</button>

                                <button className="gallery-add-btn" onClick={addImage}>➕</button>
                            </div>

                            {visibleImages.length > 1 && (
                                <div className="gallery-thumbnails">
                                    {visibleImages.map((img, idx) => (
                                        <div key={img.id ?? idx} className="thumbnail-wrapper">
                                            <img
                                                src={img.previewUrl}
                                                className={`thumbnail ${idx === currentImageIndex ? 'active' : ''}`}
                                                onClick={() => setCurrentImageIndex(idx)}
                                            />
                                            {img.isMain && <div className="thumbnail-main-badge">★</div>}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    ) : (
                        <div className="gallery-empty">
                            <p>Нет фотографий</p>
                            <button className="gallery-add-btn-empty" onClick={addImage}>➕ Добавить фото</button>
                        </div>
                    )}
                </div>

                {/* Форма редактирования */}
                <div className="farm-section">
                    <div className="farm-section-grid">
                        <div className="farm-section-left">
                            <h2 className="section-title">О ферме</h2>

                            <div className="status-control">
                                <span className="status-label">Статус фермы:</span>
                                <button className={`status-toggle ${farm?.active ? 'active' : 'inactive'}`} onClick={toggleActiveStatus}>
                                    {farm?.active ? '● Активна' : '○ Не активна'}
                                </button>
                            </div>

                            <div className="editable-field">
                                <div className="field-header">
                                    <span className="field-label">Название:</span>
                                </div>
                                <input
                                    type="text"
                                    value={editName}
                                    onChange={(e) => setEditName(e.target.value)}
                                    className="edit-input-full"
                                />
                            </div>

                            <div className="editable-field">
                                <div className="field-header">
                                    <span className="field-label">Описание:</span>
                                </div>
                                <textarea
                                    value={editDescription}
                                    onChange={(e) => setEditDescription(e.target.value)}
                                    className="edit-textarea"
                                    rows={6}
                                />
                            </div>
                        </div>

                        <div className="farm-section-right">
                            <div className="fact-item">
                                <span className="fact-icon">📅</span>
                                <span className="fact-label">Год основания:</span>
                                <input
                                    type="number"
                                    value={editEstablishedYear}
                                    onChange={(e) => setEditEstablishedYear(e.target.value)}
                                    className="edit-input-small"
                                />
                            </div>

                            <div className="fact-item">
                                <span className="fact-icon">📍</span>
                                <span className="fact-label">Регион:</span>
                                <select value={editRegion} onChange={(e) => setEditRegion(e.target.value)} className="edit-select">
                                    <option value="Швейцария">Швейцария</option>
                                    <option value="Австрия">Австрия</option>
                                    <option value="Франция">Франция</option>
                                    <option value="Италия">Италия</option>
                                    <option value="Германия">Германия</option>
                                </select>
                            </div>

                            <div className="fact-item">
                                <span className="fact-icon">📞</span>
                                <span className="fact-label">Телефон:</span>
                                <input
                                    type="text"
                                    value={editPhone}
                                    onChange={(e) => setEditPhone(e.target.value)}
                                    className="edit-input-small"
                                />
                            </div>

                            <div className="fact-item">
                                <span className="fact-icon">✉️</span>
                                <span className="fact-label">Email:</span>
                                <input
                                    type="email"
                                    value={editEmail}
                                    onChange={(e) => setEditEmail(e.target.value)}
                                    className="edit-input-small"
                                />
                            </div>

                            <div className="fact-item">
                                <span className="fact-icon">🏠</span>
                                <span className="fact-label">Виды жилья:</span>
                                <div className="fact-value editable-list">
                                    {accommodations.filter(a => a.status !== 'deleted').map((acc, idx) => (
                                        <div key={acc.id ?? idx} className="list-item">
                                            <span>{accommodationTypeMap[acc.type] || acc.type} — {acc.price}€ / неделя</span>
                                            <button className="remove-item-btn" onClick={() => removeAccommodation(idx)}>✕</button>
                                        </div>
                                    ))}
                                    <div className="add-item-row">
                                        <select value={newAccommodationType} onChange={(e) => setNewAccommodationType(e.target.value)} className="add-select">
                                            <option value="">Выберите тип</option>
                                            <option value="AGRITOURISM_ROOM">Агротуристическая комната</option>
                                            <option value="DAIRY_GUEST_ROOM">Гостевая комната на ферме</option>
                                            <option value="ALPINE_HUT">Альпийская хижина</option>
                                            <option value="APARTMENT">Апартаменты</option>
                                            <option value="TENT">Палатка</option>
                                            <option value="HOUSE">Дом</option>
                                            <option value="LODGE">Лодж</option>
                                        </select>
                                        <input
                                            type="number"
                                            placeholder="Цена €/неделя"
                                            value={newAccommodationPrice || ''}
                                            onChange={(e) => setNewAccommodationPrice(parseInt(e.target.value) || 0)}
                                            className="add-input"
                                        />
                                        <button onClick={addAccommodation} className="add-btn">+ Добавить</button>
                                    </div>
                                </div>
                            </div>

                            <div className="fact-item">
                                <span className="fact-icon">🎯</span>
                                <span className="fact-label">Работа и развлечения:</span>
                                <div className="fact-value editable-list">
                                    {activities.filter(a => a.status !== 'deleted').map((act, idx) => (
                                        <div key={act.id ?? idx} className="list-item">
                                            <span>{act.name}</span>
                                            <button className="remove-item-btn" onClick={() => removeActivity(idx)}>✕</button>
                                        </div>
                                    ))}
                                    <div className="add-item-row">
                                        <input
                                            type="text"
                                            placeholder="Новая активность"
                                            value={newActivityName}
                                            onChange={(e) => setNewActivityName(e.target.value)}
                                            className="add-input"
                                        />
                                        <button onClick={addActivity} className="add-btn">+ Добавить</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Footer />

            {hasChanges && (
                <div className="sticky-bottom-bar">
                    <div className="bottom-bar-container">
                        <button className="bottom-bar-save" onClick={handleSave} disabled={saving}>
                            {saving ? 'Сохранение...' : '💾 Сохранить изменения'}
                        </button>
                        <button className="bottom-bar-cancel" onClick={handleCancel}>
                            ✕ Отмена
                        </button>
                    </div>
                </div>
            )}

            {/* Модальные окна */}
            {showDeleteConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Подтверждение удаления</h3>
                        <p>Вы уверены, что хотите удалить это фото?</p>
                        <div className="modal-buttons">
                            <button className="modal-btn-cancel" onClick={() => setShowDeleteConfirm(false)}>Отмена</button>
                            <button className="modal-btn-confirm" onClick={deleteImage}>Да, удалить</button>
                        </div>
                    </div>
                </div>
            )}

            {showDeleteFarmConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Подтверждение удаления фермы</h3>
                        <p>Вы уверены, что хотите удалить ферму <strong>{editName}</strong>? Это действие необратимо.</p>
                        <div className="modal-buttons">
                            <button className="modal-btn-cancel" onClick={() => setShowDeleteFarmConfirm(false)}>Отмена</button>
                            <button className="modal-btn-confirm" onClick={handleDeleteFarm}>Да, удалить</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminFarmPage;