import { useEffect, useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/common/Header';
import SearchPanel from '../components/common/SearchPanel';
import FarmCard from '../components/farm/FarmCard';
import Footer from '../components/common/Footer';
import { farmApi } from '../services/api';
import type { Farm } from '../types';
import './HomePage.css';

interface HomePageProps {
    isAdmin?: boolean;
    onAdminLogin?: () => void;
    onAdminLogout?: () => void;
}

const HomePage = ({ isAdmin = false, onAdminLogin, onAdminLogout }: HomePageProps) => {
    const navigate = useNavigate();
    const [allFarms, setAllFarms] = useState<Farm[]>([]);
    const [filteredFarms, setFilteredFarms] = useState<Farm[]>([]);
    const [loading, setLoading] = useState(true);
    const [showDeleteConfirm, setShowDeleteConfirm] = useState<{ id: number; name: string } | null>(null);
    const [farmImages, setFarmImages] = useState<Map<number, string>>(new Map());
    const [hasHomepageChanges, setHasHomepageChanges] = useState(false);
    const [savingHomepage, setSavingHomepage] = useState(false);
    const [workingHours, setWorkingHours] = useState([
        { days: 'Пн - Пт', hours: '10:00 - 19:00' },
        { days: 'Сб', hours: '10:00 - 18:00' }
    ]);
    const [phones, setPhones] = useState([
        { number: '+375 (29) 369-45-89', label: 'для туристов' },
        { number: '+375 (44) 700-92-65', label: 'для агентов' }
    ]);
    const [originalWorkingHours, setOriginalWorkingHours] = useState([...workingHours]);
    const [originalPhones, setOriginalPhones] = useState([...phones]);

    // Загрузка сохраненных данных при монтировании
    useEffect(() => {
        const savedHours = localStorage.getItem('workingHours');
        if (savedHours) {
            const parsed = JSON.parse(savedHours);
            setWorkingHours(parsed);
            setOriginalWorkingHours(parsed);
        }
        const savedPhones = localStorage.getItem('phones');
        if (savedPhones) {
            const parsed = JSON.parse(savedPhones);
            setPhones(parsed);
            setOriginalPhones(parsed);
        }
    }, []);

    // Слушаем изменения в localStorage через событие storage
    useEffect(() => {
        const handleStorageChange = (e: StorageEvent) => {
            if (e.key === 'workingHours' && e.newValue) {
                const newHours = JSON.parse(e.newValue);
                setWorkingHours(newHours);
                setOriginalWorkingHours(newHours);
            }
            if (e.key === 'phones' && e.newValue) {
                const newPhones = JSON.parse(e.newValue);
                setPhones(newPhones);
                setOriginalPhones(newPhones);
            }
        };
        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    // Проверка изменений на главной странице
    useEffect(() => {
        const hoursChanged = JSON.stringify(workingHours) !== JSON.stringify(originalWorkingHours);
        const phonesChanged = JSON.stringify(phones) !== JSON.stringify(originalPhones);
        setHasHomepageChanges(hoursChanged || phonesChanged);
    }, [workingHours, phones, originalWorkingHours, originalPhones]);

    // Получаем уникальные регионы и названия из всех ферм
    const regions = useMemo(() => {
        const uniqueRegions = [...new Set(allFarms.map(f => f.region))];
        return uniqueRegions.sort();
    }, [allFarms]);

    const farmNames = useMemo(() => {
        const uniqueNames = [...new Set(allFarms.map(f => f.name))];
        return uniqueNames.sort();
    }, [allFarms]);

    const loadAllFarms = async () => {
        setLoading(true);
        try {
            const data = await farmApi.getAllFarms();
            setAllFarms(data);
            setFilteredFarms(data);

            // Загружаем главные изображения для всех ферм
            const imagesMap = new Map<number, string>();
            for (const farm of data) {
                try {
                    const farmImagesData = await farmApi.getFarmImages(farm.id);
                    const mainImage = farmImagesData.find(img => img.isMain);
                    if (mainImage) {
                        let imageUrl = mainImage.url;
                        if (!imageUrl.startsWith('http')) {
                            imageUrl = `http://localhost:8080${imageUrl}`;
                        }
                        imagesMap.set(farm.id, imageUrl);
                    } else if (farmImagesData.length > 0) {
                        let imageUrl = farmImagesData[0].url;
                        if (!imageUrl.startsWith('http')) {
                            imageUrl = `http://localhost:8080${imageUrl}`;
                        }
                        imagesMap.set(farm.id, imageUrl);
                    }
                } catch (err) {
                    console.error(`Ошибка загрузки изображения для фермы ${farm.id}:`, err);
                }
            }
            setFarmImages(imagesMap);
        } catch (err) {
            console.error('Ошибка загрузки ферм:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadAllFarms();
    }, []);

    const handleSearch = async (region: string, name: string) => {
        if (!region && !name) {
            setFilteredFarms(allFarms);
            return;
        }

        try {
            const data = await farmApi.getFarmsByFilter(region || undefined, name || undefined);
            setFilteredFarms(data);
        } catch (err) {
            console.error('Ошибка поиска:', err);
            let filtered = [...allFarms];
            if (region) filtered = filtered.filter(f => f.region === region);
            if (name) filtered = filtered.filter(f => f.name.toLowerCase().includes(name.toLowerCase()));
            setFilteredFarms(filtered);
        }
    };

    const handleFarmClick = (farmId: number) => {
        navigate(`/farms/${farmId}`);
    };

    const handleAddFarm = () => {
        console.log('handleAddFarm вызвана, isAdmin:', isAdmin);

        if (!isAdmin) {
            alert('Доступ запрещен. Только для администраторов.');
            return;
        }

        console.log('Навигация на /admin/farms/new');
        navigate('/admin/farms/new');
    };

    const handleEditFarm = (farmId: number) => {
        if (!isAdmin) {
            alert('Доступ запрещен. Только для администраторов.');
            return;
        }
        navigate(`/admin/farms/${farmId}`);
    };

    const handleDeleteFarm = async (id: number, name: string) => {
        if (!isAdmin) {
            alert('Доступ запрещен. Только для администраторов.');
            return;
        }
        setShowDeleteConfirm({ id, name });
    };

    const confirmDelete = async () => {
        if (!showDeleteConfirm) return;
        try {
            await farmApi.deleteFarm(showDeleteConfirm.id);
            await loadAllFarms();
            setShowDeleteConfirm(null);
            alert('Ферма успешно удалена');
        } catch (err) {
            console.error('Ошибка удаления:', err);
            alert('Не удалось удалить ферму. Возможно, у неё есть активные бронирования.');
        }
    };

    const handleToggleStatus = async (farm: Farm) => {
        if (!isAdmin) {
            alert('Доступ запрещен. Только для администраторов.');
            return;
        }
        try {
            await farmApi.updateFarm(farm.id, { active: !farm.active });
            await loadAllFarms();
            alert(`Ферма ${!farm.active ? 'активирована' : 'деактивирована'}`);
        } catch (err) {
            console.error('Ошибка изменения статуса:', err);
            alert('Не удалось изменить статус');
        }
    };

    // Сохранение изменений главной страницы
    const handleSaveHomepage = () => {
        setSavingHomepage(true);
        setTimeout(() => {
            setOriginalWorkingHours([...workingHours]);
            setOriginalPhones([...phones]);
            setHasHomepageChanges(false);
            setSavingHomepage(false);
            alert('Изменения успешно сохранены!');
        }, 500);
    };

    // Отмена изменений главной страницы
    const handleCancelHomepage = () => {
        setWorkingHours([...originalWorkingHours]);
        setPhones([...originalPhones]);
        setHasHomepageChanges(false);
    };

    if (loading) {
        return (
            <div>
                <Header
                    isAdmin={isAdmin}
                    onSave={handleSaveHomepage}
                    onCancel={handleCancelHomepage}
                    hasChanges={hasHomepageChanges}
                    saving={savingHomepage}
                />
                <div className="loading-spinner">Загрузка ферм...</div>
                <Footer onAdminLogin={onAdminLogin} onAdminLogout={onAdminLogout} isAdmin={isAdmin} />
            </div>
        );
    }

    return (
        <div>
            <Header
                isAdmin={isAdmin}
                onSave={handleSaveHomepage}
                onCancel={handleCancelHomepage}
                hasChanges={hasHomepageChanges}
                saving={savingHomepage}
            />
            <SearchPanel
                regions={regions}
                farmNames={farmNames}
                onSearch={handleSearch}
                isAdmin={isAdmin}
                onAddFarm={handleAddFarm}
            />

            <div className="container">
                <div className="farms-list">
                    {filteredFarms.length === 0 && (
                        <div className="no-results">
                            <p>Фермы не найдены</p>
                            {isAdmin && (
                                <button className="add-farm-empty-btn" onClick={handleAddFarm}>
                                    + Добавить первую ферму
                                </button>
                            )}
                        </div>
                    )}
                    {filteredFarms.map(farm => (
                        <FarmCard
                            key={farm.id}
                            id={farm.id}
                            name={farm.name}
                            description={farm.description || 'Описание фермы пока не добавлено'}
                            imageUrl={farmImages.get(farm.id)}
                            isActive={farm.active}
                            isAdmin={isAdmin}
                            onDetails={() => handleFarmClick(farm.id)}
                            onEdit={() => handleEditFarm(farm.id)}
                            onDelete={() => handleDeleteFarm(farm.id, farm.name)}
                            onToggleStatus={() => handleToggleStatus(farm)}
                        />
                    ))}
                </div>
            </div>

            <Footer onAdminLogin={onAdminLogin} onAdminLogout={onAdminLogout} isAdmin={isAdmin} />

            {showDeleteConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Подтверждение удаления</h3>
                        <p>Вы уверены, что хотите удалить ферму <strong>{showDeleteConfirm.name}</strong>?</p>
                        <p className="warning-text">Это действие необратимо. Все данные о ферме будут удалены.</p>
                        <div className="modal-buttons">
                            <button className="modal-btn-cancel" onClick={() => setShowDeleteConfirm(null)}>
                                Отмена
                            </button>
                            <button className="modal-btn-confirm" onClick={confirmDelete}>
                                Да, удалить
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomePage;