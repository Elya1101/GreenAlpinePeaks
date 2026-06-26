import { useEffect, useState, useRef, useCallback } from 'react';
import { useParams, useNavigate, useLocation, useBeforeUnload } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import NotificationModal from '../components/common/NotificationModal';
import { farmApi, regionApi, accommodationTypeApi, activitiesApi } from '../services/api';
import type { Farm, Region, AccommodationType, Activity } from '../types';
import { cleanPhoneNumber, isValidPhoneNumber, getPhoneErrorMessage } from '../utils/phoneHelper';
import {
    Calendar, MapPin, Home, Briefcase, Phone, Mail,
    Trash2, Plus, Star, ImagePlus, X, Check, ChevronLeft, ChevronRight
} from 'lucide-react';
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
    typeId: number;
    typeName: string;
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

const useRegionManagement = () => {
    const [regions, setRegions] = useState<Region[]>([]);
    const [selectedRegionId, setSelectedRegionId] = useState<number | null>(null);
    const [selectedRegionName, setSelectedRegionName] = useState<string>('');
    const [isCreatingNewRegion, setIsCreatingNewRegion] = useState(false);
    const [newRegionName, setNewRegionName] = useState('');
    const [isSavingRegion, setIsSavingRegion] = useState(false);

    const loadRegions = useCallback(async () => {
        try {
            const data = await regionApi.getAllRegions();
            setRegions(data);
        } catch (err) {
            console.error('Ошибка загрузки регионов:', err);
        }
    }, []);

    const createRegion = useCallback(async (name: string): Promise<Region> => {
        setIsSavingRegion(true);
        try {
            const newRegion = await regionApi.createRegion({ name });
            setRegions(prev => [...prev, newRegion]);
            return newRegion;
        } finally {
            setIsSavingRegion(false);
        }
    }, []);

    return {
        regions,
        selectedRegionId,
        setSelectedRegionId,
        selectedRegionName,
        setSelectedRegionName,
        isCreatingNewRegion,
        setIsCreatingNewRegion,
        newRegionName,
        setNewRegionName,
        isSavingRegion,
        loadRegions,
        createRegion,
    };
};

const useActivitiesManagement = () => {
    const [allActivities, setAllActivities] = useState<Activity[]>([]);
    const [selectedActivityId, setSelectedActivityId] = useState<number | null>(null);
    const [isCreatingNewActivity, setIsCreatingNewActivity] = useState(false);
    const [newActivityName, setNewActivityName] = useState('');
    const [isSavingActivity, setIsSavingActivity] = useState(false);
    const [farmActivities, setFarmActivities] = useState<ActivityWithStatus[]>([]);

    const loadAllActivities = useCallback(async () => {
        try {
            const data = await activitiesApi.getAllActivities();
            setAllActivities(data);
        } catch (err) {
            console.error('Ошибка загрузки активностей:', err);
        }
    }, []);

    const createActivity = useCallback(async (name: string): Promise<Activity> => {
        setIsSavingActivity(true);
        try {
            const newActivity = await activitiesApi.createActivity({ name });
            setAllActivities(prev => [...prev, newActivity]);
            return newActivity;
        } finally {
            setIsSavingActivity(false);
        }
    }, []);

    const addExistingActivityToFarm = useCallback((activityId: number) => {
        const activity = allActivities.find(a => a.id === activityId);
        if (activity && !farmActivities.some(fa => fa.id === activityId && fa.status !== 'deleted')) {
            setFarmActivities(prev => [...prev, {
                id: activity.id,
                name: activity.name,
                status: 'new'
            }]);
            setSelectedActivityId(null);
        }
    }, [allActivities, farmActivities]);

    const createAndAddActivity = useCallback(async (name: string): Promise<void> => {
        if (!name.trim()) return;

        try {
            const newActivity = await createActivity(name.trim());
            setFarmActivities(prev => [...prev, {
                id: newActivity.id,
                name: newActivity.name,
                status: 'new'
            }]);
            setIsCreatingNewActivity(false);
            setNewActivityName('');
        } catch (err: any) {
            console.error('Ошибка создания активности:', err);
            throw err;
        }
    }, [createActivity]);

    const removeActivity = useCallback((index: number) => {
        setFarmActivities(prev => {
            const activity = prev[index];
            if (activity.id) {
                return prev.map((a, i) =>
                    i === index ? { ...a, status: 'deleted' } : a
                );
            } else {
                return prev.filter((_, i) => i !== index);
            }
        });
    }, []);

    const loadActivitiesFromFarm = useCallback((farmActivitiesData: Activity[]) => {
        setFarmActivities(farmActivitiesData.map(a => ({
            id: a.id,
            name: a.name,
            status: 'existing'
        })));
    }, []);

    const saveActivities = useCallback(async (farmId: number) => {
        const toDelete = farmActivities.filter(a => a.status === 'deleted' && a.id);
        const toAdd = farmActivities.filter(a => a.status === 'new' && a.id);

        for (const activity of toDelete) {
            if (activity.id) {
                await farmApi.removeActivityFromFarm(farmId, activity.id);
            }
        }

        for (const activity of toAdd) {
            if (activity.id) {
                await farmApi.addActivityToFarm(farmId, activity.id);
            }
        }

        setFarmActivities(prev => prev.map(a => ({
            ...a,
            status: 'existing'
        })));
    }, [farmActivities]);

    return {
        allActivities,
        selectedActivityId,
        setSelectedActivityId,
        farmActivities,
        setFarmActivities,
        isCreatingNewActivity,
        setIsCreatingNewActivity,
        newActivityName,
        setNewActivityName,
        isSavingActivity,
        loadAllActivities,
        createActivity,
        addExistingActivityToFarm,
        createAndAddActivity,
        removeActivity,
        loadActivitiesFromFarm,
        saveActivities
    };
};

const useAccommodationsManagement = () => {
    const [accommodations, setAccommodations] = useState<AccommodationWithStatus[]>([]);
    const [newAccommodationTypeId, setNewAccommodationTypeId] = useState<number | null>(null);
    const [newAccommodationPrice, setNewAccommodationPrice] = useState<number>(0);
    const [accommodationTypes, setAccommodationTypes] = useState<AccommodationType[]>([]);
    const [isTypesLoaded, setIsTypesLoaded] = useState(false);

    const loadAccommodationTypes = useCallback(async () => {
        try {
            const types = await accommodationTypeApi.getAllTypes();
            setAccommodationTypes(types);
            setIsTypesLoaded(true);
        } catch (err) {
            console.error('Ошибка загрузки типов жилья:', err);
        }
    }, []);

    const addAccommodation = useCallback((typeId: number, price: number, typeName: string) => {
        if (!typeId || price <= 0) return;

        setAccommodations(prev => [...prev, {
            typeId,
            typeName,
            price,
            status: 'new'
        }]);
    }, []);

    const removeAccommodation = useCallback((index: number) => {
        setAccommodations(prev => {
            const accommodation = prev[index];
            if (accommodation.id) {
                return prev.map((a, i) =>
                    i === index ? { ...a, status: 'deleted' } : a
                );
            } else {
                return prev.filter((_, i) => i !== index);
            }
        });
    }, []);

    const loadAccommodationsFromFarm = useCallback((farmAccommodations: any[], types: AccommodationType[]) => {
        setAccommodations(farmAccommodations.map(a => {
            const type = types.find(t => t.id === a.typeId);
            return {
                id: a.id,
                typeId: a.typeId,
                typeName: a.typeName || type?.name || 'Неизвестный тип',
                price: a.price,
                status: 'existing'
            };
        }));
    }, []);

    const saveAccommodations = useCallback(async (farmId: number) => {
        const toDelete = accommodations.filter(a => a.status === 'deleted' && a.id);
        const toAdd = accommodations.filter(a => a.status === 'new');

        for (const accommodation of toDelete) {
            if (accommodation.id) {
                await farmApi.deleteAccommodation(accommodation.id);
            }
        }

        for (const accommodation of toAdd) {
            await farmApi.addAccommodationToFarm(farmId, accommodation.typeId, accommodation.price);
        }
    }, [accommodations]);

    return {
        accommodations,
        setAccommodations,
        newAccommodationTypeId,
        setNewAccommodationTypeId,
        newAccommodationPrice,
        setNewAccommodationPrice,
        accommodationTypes,
        isTypesLoaded,
        loadAccommodationTypes,
        addAccommodation,
        removeAccommodation,
        loadAccommodationsFromFarm,
        saveAccommodations
    };
};

const useImagesManagement = () => {
    const [images, setImages] = useState<ImageData[]>([]);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

    const addImage = useCallback((file: File, previewUrl: string, isMain: boolean) => {
        setImages(prev => [...prev, {
            file,
            previewUrl,
            isMain,
            status: 'new'
        }]);
    }, []);

    const deleteImage = useCallback((imageToDelete: ImageData) => {
        setImages(prev => {
            if (imageToDelete.id) {
                return prev.map(img =>
                    img === imageToDelete ? { ...img, status: 'deleted' } : img
                );
            } else {
                return prev.filter(img => img !== imageToDelete);
            }
        });
    }, []);

    const setAsMainImage = useCallback((index: number) => {
        setImages(prev => prev.map((img, i) => ({
            ...img,
            isMain: i === index
        })));
    }, []);

    const loadImagesFromFarm = useCallback(async (farmId: number) => {
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
            throw err;
        }
    }, []);

    const saveImages = useCallback(async (farmId: number) => {
        const toDelete = images.filter(img => img.status === 'deleted' && img.id);
        for (const img of toDelete) {
            if (img.id) {
                await farmApi.deleteImage(farmId, img.id);
            }
        }

        const toUpload = images.filter(img => img.status === 'new');
        for (const img of toUpload) {
            if (img.file) {
                await farmApi.uploadImage(farmId, img.file, img.isMain);
            }
        }

        const mainImage = images.find(img => img.isMain && img.id);
        if (mainImage?.id) {
            await farmApi.setMainImage(farmId, mainImage.id);
        }

        await loadImagesFromFarm(farmId);
    }, [images, loadImagesFromFarm]);

    const getVisibleImages = useCallback(() => images.filter(img => img.status !== 'deleted'), [images]);

    return {
        images,
        currentImageIndex,
        setCurrentImageIndex,
        showDeleteConfirm,
        setShowDeleteConfirm,
        addImage,
        deleteImage,
        setAsMainImage,
        loadImagesFromFarm,
        saveImages,
        getVisibleImages
    };
};

interface AdminFarmPageProps {
    onAdminLogout?: () => void;
}

const AdminFarmPage = ({ onAdminLogout }: AdminFarmPageProps) => {
    const { id } = useParams<{ id: string }>();
    const location = useLocation();
    const navigate = useNavigate();

    const isNewFarm = location.pathname === '/admin/farms/new';
    const fileInputRef = useRef<HTMLInputElement>(null);

    const [farm, setFarm] = useState<Farm | null>(null);
    const [originalFarm, setOriginalFarm] = useState<Farm | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [hasChanges, setHasChanges] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [showNotification, setShowNotification] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

    const [editName, setEditName] = useState('');
    const [editDescription, setEditDescription] = useState('');
    const [editEstablishedYear, setEditEstablishedYear] = useState('');
    const [editEmail, setEditEmail] = useState('');
    const [editPhone, setEditPhone] = useState('');
    const [phoneError, setPhoneError] = useState<string | null>(null);

    const regionManager = useRegionManagement();
    const activitiesManager = useActivitiesManagement();
    const accommodationsManager = useAccommodationsManagement();
    const imagesManager = useImagesManagement();

    useEffect(() => {
        const loadSelectData = async () => {
            try {
                await Promise.all([
                    regionManager.loadRegions(),
                    activitiesManager.loadAllActivities(),
                    accommodationsManager.loadAccommodationTypes()
                ]);
            } catch (err) {
                console.error('Ошибка загрузки справочников:', err);
            }
        };
        loadSelectData();
    }, []);

    useEffect(() => {
        const loadFarmData = async (farmId: number) => {
            try {
                const data = await farmApi.getFarmById(farmId);
                setFarm(data);
                setOriginalFarm(JSON.parse(JSON.stringify(data)));

                setEditName(data.name);
                setEditDescription(data.description || '');
                setEditEstablishedYear(String(data.establishedYear || ''));
                regionManager.setSelectedRegionId(data.regionId);
                regionManager.setSelectedRegionName(data.regionName || '');
                setEditEmail(data.email || '');
                setEditPhone(data.phone || '');

                activitiesManager.loadActivitiesFromFarm(data.activities);
                accommodationsManager.loadAccommodationsFromFarm(data.accommodations, accommodationsManager.accommodationTypes);
                await imagesManager.loadImagesFromFarm(farmId);
            } catch (err) {
                console.error('Ошибка загрузки фермы:', err);
                throw err;
            }
        };

        const loadFarm = async () => {
            try {
                if (isNewFarm) {
                    setLoading(false);
                    return;
                }

                if (!id) {
                    setError('ID фермы не указан');
                    setLoading(false);
                    return;
                }

                const farmId = parseInt(id);
                if (isNaN(farmId)) {
                    setError('Некорректный ID фермы');
                    setLoading(false);
                    return;
                }

                if (!accommodationsManager.isTypesLoaded) {
                    await accommodationsManager.loadAccommodationTypes();
                }

                await loadFarmData(farmId);
                setError(null);
            } catch (err) {
                console.error('Ошибка в loadFarm:', err);
                setError('Не удалось загрузить данные фермы');
            } finally {
                setLoading(false);
            }
        };

        loadFarm();
    }, [id, isNewFarm, location.pathname]);

    useEffect(() => {
        if (!farm && !isNewFarm) return;

        const hasNameChange = editName !== (originalFarm?.name || '');
        const hasDescChange = editDescription !== (originalFarm?.description || '');
        const hasYearChange = editEstablishedYear !== String(originalFarm?.establishedYear || '');
        const hasRegionChange = regionManager.selectedRegionId !== (originalFarm?.regionId || null);
        const hasPhoneChange = editPhone !== (originalFarm?.phone || '');
        const hasEmailChange = editEmail !== (originalFarm?.email || '');

        const hasActivityChanges = activitiesManager.farmActivities.some(a => a.status !== 'existing');
        const hasAccommodationChanges = accommodationsManager.accommodations.some(a => a.status !== 'existing');
        const hasImageChanges = imagesManager.images.some(img => img.status !== 'existing');

        setHasChanges(hasNameChange || hasDescChange || hasYearChange ||
            hasRegionChange || hasPhoneChange || hasEmailChange ||
            hasActivityChanges || hasAccommodationChanges || hasImageChanges);
    }, [editName, editDescription, editEstablishedYear, regionManager.selectedRegionId,
        editPhone, editEmail, activitiesManager.farmActivities, accommodationsManager.accommodations,
        imagesManager.images, farm, originalFarm, isNewFarm]);

    useBeforeUnload(
        useCallback(() => {
            if (hasChanges) {
                return 'У вас есть несохраненные изменения. Вы уверены, что хотите покинуть страницу?';
            }
        }, [hasChanges])
    );

    const handlePhoneChange = (value: string) => {
        setEditPhone(value);
        if (value && !isValidPhoneNumber(value)) {
            setPhoneError(getPhoneErrorMessage(value));
        } else {
            setPhoneError(null);
        }
    };

    const closeNotification = () => {
        setShowNotification(null);
    };

    const handleSave = async () => {
        setSaving(true);
        setError(null);

        try {
            if (!editName.trim()) {
                setError('Название фермы обязательно для заполнения');
                setSaving(false);
                return;
            }

            let finalRegionName: string;

            if (regionManager.isCreatingNewRegion && regionManager.newRegionName.trim()) {
                const newRegion = await regionManager.createRegion(regionManager.newRegionName.trim());
                finalRegionName = newRegion.name;
                regionManager.setSelectedRegionId(newRegion.id);
                regionManager.setSelectedRegionName(newRegion.name);
            } else if (regionManager.selectedRegionId) {
                const selectedRegion = regionManager.regions.find(r => r.id === regionManager.selectedRegionId);
                finalRegionName = selectedRegion?.name || '';
            } else {
                setError('Регион обязателен для заполнения');
                setSaving(false);
                return;
            }

            if (editPhone && editPhone.trim() && !isValidPhoneNumber(editPhone)) {
                const errorMsg = getPhoneErrorMessage(editPhone);
                setError(errorMsg || 'Неверный формат телефона');
                setSaving(false);
                return;
            }

            if (editEmail && editEmail.trim()) {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(editEmail.trim())) {
                    setError('Введите корректный email адрес');
                    setSaving(false);
                    return;
                }
            }

            const updateData: any = {
                name: editName.trim(),
                active: true,
                region: finalRegionName,
            };

            if (editDescription && editDescription.trim()) {
                updateData.description = editDescription.trim();
            }

            if (editEstablishedYear) {
                const year = parseInt(editEstablishedYear);
                if (!isNaN(year) && year > 1800 && year <= new Date().getFullYear()) {
                    updateData.establishedYear = year;
                }
            }

            if (editPhone && editPhone.trim()) {
                updateData.phone = cleanPhoneNumber(editPhone.trim());
            }

            if (editEmail && editEmail.trim()) {
                updateData.email = editEmail.trim();
            }

            let farmId: number;

            if (isNewFarm) {
                const newFarm = await farmApi.createFarm(updateData);
                farmId = newFarm.id;

                const hasNewAccommodations = accommodationsManager.accommodations.some(a => a.status === 'new');
                if (hasNewAccommodations) {
                    await accommodationsManager.saveAccommodations(farmId);
                }

                const hasNewActivities = activitiesManager.farmActivities.some(a => a.status === 'new' && a.id);
                if (hasNewActivities) {
                    await activitiesManager.saveActivities(farmId);
                }

                const hasNewImages = imagesManager.images.some(img => img.status === 'new');
                if (hasNewImages) {
                    await imagesManager.saveImages(farmId);
                }

                setShowNotification({
                    message: `✅ Ферма "${editName.trim()}" успешно создана!`,
                    type: 'success'
                });

                setTimeout(() => {
                    navigate('/');
                }, 1500);

            } else if (farm) {
                await farmApi.updateFarm(farm.id, updateData);
                await activitiesManager.saveActivities(farm.id);
                await accommodationsManager.saveAccommodations(farm.id);
                await imagesManager.saveImages(farm.id);

                const updatedFarm = await farmApi.getFarmById(farm.id);
                setFarm(updatedFarm);
                setOriginalFarm(JSON.parse(JSON.stringify(updatedFarm)));
                setHasChanges(false);

                setShowNotification({
                    message: `✅ Изменения для фермы "${editName.trim()}" успешно сохранены!`,
                    type: 'success'
                });

                setTimeout(() => {
                    navigate('/');
                }, 1500);
            }
        } catch (err: any) {
            console.error('Ошибка сохранения:', err);
            let errorMessage = '❌ Не удалось сохранить изменения';
            if (err.response?.data?.message) {
                errorMessage = `❌ ${err.response.data.message}`;
            } else if (err.response?.data?.error) {
                errorMessage = `❌ ${err.response.data.error}`;
            } else if (err.message) {
                errorMessage = `❌ ${err.message}`;
            }
            setShowNotification({ message: errorMessage, type: 'error' });
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

    const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (event) => {
            const visibleImages = imagesManager.getVisibleImages();
            imagesManager.addImage(
                file,
                event.target?.result as string,
                visibleImages.length === 0
            );
        };
        reader.readAsDataURL(file);

        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const handleDeleteImage = () => {
        const visibleImages = imagesManager.getVisibleImages();
        const imageToDelete = visibleImages[imagesManager.currentImageIndex];
        if (imageToDelete) {
            imagesManager.deleteImage(imageToDelete);
            imagesManager.setShowDeleteConfirm(false);

            const remainingVisible = imagesManager.getVisibleImages();
            if (imagesManager.currentImageIndex >= remainingVisible.length) {
                imagesManager.setCurrentImageIndex(Math.max(0, remainingVisible.length - 1));
            }
        }
    };

    const toggleActiveStatus = async () => {
        if (!farm || isNewFarm) {
            setFarm(prev => prev ? { ...prev, active: !prev.active } : null);
            return;
        }
        try {
            await farmApi.updateFarm(farm.id, { active: !farm.active });
            const updatedFarm = { ...farm, active: !farm.active };
            setFarm(updatedFarm);
            if (originalFarm) {
                setOriginalFarm({ ...originalFarm, active: !farm.active });
            }
            setShowNotification({
                message: `✅ Ферма "${farm.name}" ${!farm.active ? 'активирована' : 'деактивирована'}`,
                type: 'success'
            });
        } catch (err) {
            console.error('Ошибка изменения статуса:', err);
            setShowNotification({ message: '❌ Не удалось изменить статус фермы', type: 'error' });
        }
    };

    const visibleImages = imagesManager.getVisibleImages();

    if (loading) {
        return (
            <div>
                <Header isAdmin={true} />
                <div className="loading-spinner">Загрузка...</div>
                <Footer isAdmin={true} onAdminLogin={() => {}} onAdminLogout={onAdminLogout} />
            </div>
        );
    }

    if (error && !farm && !isNewFarm) {
        return (
            <div>
                <Header isAdmin={true} />
                <div className="error-message">
                    <p>{error}</p>
                    <button className="back-button" onClick={() => navigate('/')}>
                        ← Вернуться на главную
                    </button>
                </div>
                <Footer isAdmin={true} onAdminLogin={() => {}} onAdminLogout={onAdminLogout} />
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
                    <h1 className="farm-hero-title">
                        {isNewFarm ? (editName || 'Новая ферма') : editName}
                    </h1>
                </div>
            </div>

            <div className="container">
                <div className="admin-nav-buttons">
                    <button className="back-button" onClick={() => navigate('/')}>
                        ← На главную
                    </button>
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="farm-gallery admin-gallery">
                    {visibleImages.length > 0 ? (
                        <>
                            <div className="gallery-container">
                                <button
                                    className="gallery-nav prev"
                                    onClick={() => {
                                        imagesManager.setCurrentImageIndex(
                                            (imagesManager.currentImageIndex - 1 + visibleImages.length) % visibleImages.length
                                        );
                                    }}
                                >
                                    <ChevronLeft size={24} strokeWidth={2} />
                                </button>

                                <div className="gallery-image-wrapper">
                                    <img
                                        src={visibleImages[imagesManager.currentImageIndex]?.previewUrl}
                                        alt="Ферма"
                                        className="gallery-image"
                                    />
                                    {visibleImages[imagesManager.currentImageIndex]?.isMain && (
                                        <div className="main-image-badge">
                                            <Star size={12} fill="gold" color="gold" /> Главное
                                        </div>
                                    )}
                                    <button
                                        className="gallery-delete-btn"
                                        onClick={() => imagesManager.setShowDeleteConfirm(true)}
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                    {!visibleImages[imagesManager.currentImageIndex]?.isMain && (
                                        <button
                                            className="gallery-set-main-btn"
                                            onClick={() => imagesManager.setAsMainImage(imagesManager.currentImageIndex)}
                                        >
                                            <Star size={18} />
                                        </button>
                                    )}
                                </div>

                                <button
                                    className="gallery-nav next"
                                    onClick={() => {
                                        imagesManager.setCurrentImageIndex(
                                            (imagesManager.currentImageIndex + 1) % visibleImages.length
                                        );
                                    }}
                                >
                                    <ChevronRight size={24} strokeWidth={2} />
                                </button>

                                <button className="gallery-add-btn" onClick={() => fileInputRef.current?.click()}>
                                    <ImagePlus size={20} />
                                </button>
                            </div>

                            {visibleImages.length > 1 && (
                                <div className="gallery-thumbnails">
                                    {visibleImages.map((img, idx) => (
                                        <div key={img.id ?? idx} className="thumbnail-wrapper">
                                            <img
                                                src={img.previewUrl}
                                                className={`thumbnail ${idx === imagesManager.currentImageIndex ? 'active' : ''}`}
                                                onClick={() => imagesManager.setCurrentImageIndex(idx)}
                                            />
                                            {img.isMain && <div className="thumbnail-main-badge">★</div>}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    ) : (
                        <div className="gallery-empty">
                            <ImagePlus size={48} strokeWidth={1} color="#CBD5E0" />
                            <p>Нет фотографий</p>
                            <button className="gallery-add-btn-empty" onClick={() => fileInputRef.current?.click()}>
                                <Plus size={16} /> Добавить фото
                            </button>
                        </div>
                    )}
                </div>

                <div className="farm-section">
                    <div className="farm-section-grid">
                        <div className="farm-section-left">
                            <h2 className="section-title">О ферме</h2>

                            <div className="status-control">
                                <span className="status-label">Статус фермы:</span>
                                <button
                                    className={`status-toggle ${farm?.active !== false ? 'active' : 'inactive'}`}
                                    onClick={toggleActiveStatus}
                                >
                                    {farm?.active !== false ? '● Активна' : '○ Не активна'}
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
                                    placeholder="Введите название фермы"
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
                                    placeholder="Опишите ферму, её особенности и преимущества..."
                                />
                            </div>
                        </div>

                        <div className="farm-section-right">
                            <div className="fact-item">
                                <Calendar size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Год основания:</span>
                                <input
                                    type="number"
                                    value={editEstablishedYear}
                                    onChange={(e) => setEditEstablishedYear(e.target.value)}
                                    className="edit-input-small"
                                    placeholder="Год"
                                />
                            </div>

                            <div className="fact-item">
                                <MapPin size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Регион:</span>
                                <div className="fact-value">
                                    {!regionManager.isCreatingNewRegion ? (
                                        <>
                                            <select
                                                value={regionManager.selectedRegionId || ''}
                                                onChange={(e) => {
                                                    const id = parseInt(e.target.value);
                                                    regionManager.setSelectedRegionId(id || null);
                                                    const region = regionManager.regions.find(r => r.id === id);
                                                    if (region) {
                                                        regionManager.setSelectedRegionName(region.name);
                                                    }
                                                }}
                                                className="edit-select-full"
                                            >
                                                <option value="">Выберите регион</option>
                                                {regionManager.regions.map(region => (
                                                    <option key={region.id} value={region.id}>
                                                        {region.name}
                                                    </option>
                                                ))}
                                            </select>
                                            <button
                                                type="button"
                                                onClick={() => regionManager.setIsCreatingNewRegion(true)}
                                                className="add-btn-outline"
                                            >
                                                <Plus size={14} /> Создать новый регион
                                            </button>
                                        </>
                                    ) : (
                                        <div className="new-item-form">
                                            <input
                                                type="text"
                                                value={regionManager.newRegionName}
                                                onChange={(e) => regionManager.setNewRegionName(e.target.value)}
                                                className="edit-input-full"
                                                placeholder="Введите название нового региона"
                                                autoFocus
                                                onKeyPress={(e) => {
                                                    if (e.key === 'Enter' && regionManager.newRegionName.trim()) {
                                                        regionManager.createRegion(regionManager.newRegionName)
                                                            .then(newRegion => {
                                                                regionManager.setSelectedRegionId(newRegion.id);
                                                                regionManager.setSelectedRegionName(newRegion.name);
                                                                regionManager.setIsCreatingNewRegion(false);
                                                                regionManager.setNewRegionName('');
                                                            })
                                                            .catch(err => setError('Не удалось создать регион: ' + err.message));
                                                    }
                                                }}
                                            />
                                            <div className="new-item-actions">
                                                <button
                                                    type="button"
                                                    onClick={async () => {
                                                        if (regionManager.newRegionName.trim()) {
                                                            try {
                                                                const newRegion = await regionManager.createRegion(regionManager.newRegionName);
                                                                regionManager.setSelectedRegionId(newRegion.id);
                                                                regionManager.setSelectedRegionName(newRegion.name);
                                                                regionManager.setIsCreatingNewRegion(false);
                                                                regionManager.setNewRegionName('');
                                                            } catch (err: any) {
                                                                setError('Не удалось создать регион: ' + err.message);
                                                            }
                                                        }
                                                    }}
                                                    className="add-btn-confirm"
                                                    disabled={regionManager.isSavingRegion}
                                                >
                                                    <Check size={14} /> {regionManager.isSavingRegion ? 'Создание...' : 'Сохранить'}
                                                </button>
                                                <button
                                                    type="button"
                                                    onClick={() => {
                                                        regionManager.setIsCreatingNewRegion(false);
                                                        regionManager.setNewRegionName('');
                                                    }}
                                                    className="add-btn-cancel"
                                                >
                                                    <X size={14} /> Отмена
                                                </button>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>

                            <div className="fact-item">
                                <Home size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Виды жилья:</span>
                                <div className="fact-value editable-list">
                                    {accommodationsManager.accommodations
                                        .filter(a => a.status !== 'deleted')
                                        .map((acc, idx) => (
                                            <div key={acc.id ?? idx} className="list-item">
                                                <span>
                                                    {acc.typeName || accommodationsManager.accommodationTypes.find(t => t.id === acc.typeId)?.name || 'Загрузка...'}
                                                    — {acc.price}€ / неделя
                                                </span>
                                                <button
                                                    className="remove-item-btn"
                                                    onClick={() => accommodationsManager.removeAccommodation(idx)}
                                                >
                                                    <X size={14} />
                                                </button>
                                            </div>
                                        ))}

                                    <div className="add-item-row">
                                        <select
                                            value={accommodationsManager.newAccommodationTypeId || ''}
                                            onChange={(e) => accommodationsManager.setNewAccommodationTypeId(parseInt(e.target.value))}
                                            className="add-select"
                                        >
                                            <option value="">Выберите тип жилья</option>
                                            {accommodationsManager.accommodationTypes.map(type => (
                                                <option key={type.id} value={type.id}>
                                                    {accommodationTypeMap[type.code] || type.name}
                                                </option>
                                            ))}
                                        </select>

                                        <input
                                            type="number"
                                            placeholder="Цена €/неделя"
                                            value={accommodationsManager.newAccommodationPrice || ''}
                                            onChange={(e) => accommodationsManager.setNewAccommodationPrice(parseInt(e.target.value) || 0)}
                                            className="add-input-price"
                                        />
                                        <button
                                            onClick={() => {
                                                const selectedType = accommodationsManager.accommodationTypes.find(t => t.id === accommodationsManager.newAccommodationTypeId);
                                                if (selectedType && accommodationsManager.newAccommodationTypeId && accommodationsManager.newAccommodationPrice > 0) {
                                                    accommodationsManager.addAccommodation(
                                                        accommodationsManager.newAccommodationTypeId,
                                                        accommodationsManager.newAccommodationPrice,
                                                        accommodationTypeMap[selectedType.code] || selectedType.name
                                                    );
                                                    accommodationsManager.setNewAccommodationTypeId(null);
                                                    accommodationsManager.setNewAccommodationPrice(0);
                                                }
                                            }}
                                            className="add-btn-primary"
                                        >
                                            <Plus size={14} /> Добавить
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div className="fact-item">
                                <Briefcase size={18} className="fact-icon" strokeWidth={1.5} />
                                <span className="fact-label">Работа и развлечения:</span>
                                <div className="fact-value editable-list">
                                    {activitiesManager.farmActivities
                                        .filter(a => a.status !== 'deleted')
                                        .map((act, idx) => (
                                            <div key={act.id ?? idx} className="list-item">
                                                <span>{act.name}</span>
                                                <button
                                                    className="remove-item-btn"
                                                    onClick={() => activitiesManager.removeActivity(idx)}
                                                >
                                                    <X size={14} />
                                                </button>
                                            </div>
                                        ))}

                                    {!activitiesManager.isCreatingNewActivity ? (
                                        <>
                                            <div className="add-item-row">
                                                <select
                                                    value={activitiesManager.selectedActivityId || ''}
                                                    onChange={(e) => {
                                                        const id = parseInt(e.target.value);
                                                        activitiesManager.setSelectedActivityId(id || null);
                                                    }}
                                                    className="add-select-full"
                                                >
                                                    <option value="">Выберите активность</option>
                                                    {activitiesManager.allActivities
                                                        .filter(a => !activitiesManager.farmActivities.some(fa => fa.id === a.id && fa.status !== 'deleted'))
                                                        .map(activity => (
                                                            <option key={activity.id} value={activity.id}>
                                                                {activity.name}
                                                            </option>
                                                        ))}
                                                </select>
                                                <button
                                                    onClick={() => {
                                                        if (activitiesManager.selectedActivityId) {
                                                            activitiesManager.addExistingActivityToFarm(activitiesManager.selectedActivityId);
                                                        }
                                                    }}
                                                    className="add-btn-primary"
                                                    disabled={!activitiesManager.selectedActivityId}
                                                >
                                                    <Plus size={14} /> Добавить
                                                </button>
                                            </div>
                                            <button
                                                type="button"
                                                onClick={() => activitiesManager.setIsCreatingNewActivity(true)}
                                                className="add-btn-outline"
                                                style={{ width: '100%' }}
                                            >
                                                <Plus size={14} /> Создать новую активность
                                            </button>
                                        </>
                                    ) : (
                                        <div className="new-item-form">
                                            <input
                                                type="text"
                                                placeholder="Введите название новой активности"
                                                value={activitiesManager.newActivityName}
                                                onChange={(e) => activitiesManager.setNewActivityName(e.target.value)}
                                                className="edit-input-full"
                                                autoFocus
                                                onKeyPress={(e) => {
                                                    if (e.key === 'Enter' && activitiesManager.newActivityName.trim()) {
                                                        activitiesManager.createAndAddActivity(activitiesManager.newActivityName)
                                                            .catch(err => setError(err.message));
                                                    }
                                                }}
                                            />
                                            <div className="new-item-actions">
                                                <button
                                                    type="button"
                                                    onClick={async () => {
                                                        if (activitiesManager.newActivityName.trim()) {
                                                            try {
                                                                await activitiesManager.createAndAddActivity(activitiesManager.newActivityName);
                                                            } catch (err: any) {
                                                                setError(err.message);
                                                            }
                                                        }
                                                    }}
                                                    className="add-btn-confirm"
                                                    disabled={activitiesManager.isSavingActivity}
                                                >
                                                    <Check size={14} /> {activitiesManager.isSavingActivity ? 'Создание...' : 'Создать'}
                                                </button>
                                                <button
                                                    type="button"
                                                    onClick={() => {
                                                        activitiesManager.setIsCreatingNewActivity(false);
                                                        activitiesManager.setNewActivityName('');
                                                    }}
                                                    className="add-btn-cancel"
                                                >
                                                    <X size={14} /> Отмена
                                                </button>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="farm-contacts-section">
                    <h2 className="section-title">Контактные данные фермы</h2>
                    <div className="contacts-grid">
                        <div className="contact-field">
                            <Phone size={18} className="contact-field-icon" strokeWidth={1.5} />
                            <div className="contact-field-content">
                                <span className="contact-field-label">Телефон:</span>
                                <input
                                    type="tel"
                                    value={editPhone}
                                    onChange={(e) => handlePhoneChange(e.target.value)}
                                    className={`contact-input ${phoneError ? 'input-error' : ''}`}
                                    placeholder="+375 (29) 123-45-67"
                                />
                                {phoneError && <small className="error-hint">{phoneError}</small>}
                                <small className="phone-hint">Можно вводить в любом формате</small>
                            </div>
                        </div>
                        <div className="contact-field">
                            <Mail size={18} className="contact-field-icon" strokeWidth={1.5} />
                            <div className="contact-field-content">
                                <span className="contact-field-label">Email:</span>
                                <input
                                    type="email"
                                    value={editEmail}
                                    onChange={(e) => setEditEmail(e.target.value)}
                                    className="contact-input"
                                    placeholder="example@domain.com"
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <Footer
                isAdmin={true}
                onAdminLogin={() => {}}
                onAdminLogout={onAdminLogout}
            />

            {hasChanges && (
                <div className="sticky-bottom-bar">
                    <div className="bottom-bar-container">
                        <button className="bottom-bar-save" onClick={handleSave} disabled={saving}>
                            {saving ? '💾 Сохранение...' : (isNewFarm ? 'Создать ферму' : 'Сохранить изменения')}
                        </button>
                        <button className="bottom-bar-cancel" onClick={handleCancel}>
                            ✕ Отмена
                        </button>
                    </div>
                </div>
            )}

            {imagesManager.showDeleteConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Подтверждение удаления</h3>
                        <p>Вы уверены, что хотите удалить это фото?</p>
                        <div className="modal-buttons">
                            <button className="modal-btn-cancel" onClick={() => imagesManager.setShowDeleteConfirm(false)}>
                                Отмена
                            </button>
                            <button className="modal-btn-confirm" onClick={handleDeleteImage}>
                                Да, удалить
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {showNotification && (
                <NotificationModal
                    message={showNotification.message}
                    type={showNotification.type}
                    onClose={closeNotification}
                />
            )}
        </div>
    );
};

export default AdminFarmPage;