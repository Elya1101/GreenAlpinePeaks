import { useEffect, useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import Header from '../components/common/Header';
import SearchPanel from '../components/common/SearchPanel';
import FarmCard from '../components/farm/FarmCard';
import Footer from '../components/common/Footer';
import NotificationModal from '../components/common/NotificationModal';
import { farmApi, regionApi } from '../services/api';
import type { Farm, Region } from '../types';
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
    const [showNotification, setShowNotification] = useState<{ message: string; type: 'success' | 'error' } | null>(null);
    const [farmImages, setFarmImages] = useState<Map<number, string>>(new Map());
    const [hasHomepageChanges, setHasHomepageChanges] = useState(false);
    const [savingHomepage, setSavingHomepage] = useState(false);

    const [allRegions, setAllRegions] = useState<Region[]>([]);

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

    useEffect(() => {
        const hoursChanged = JSON.stringify(workingHours) !== JSON.stringify(originalWorkingHours);
        const phonesChanged = JSON.stringify(phones) !== JSON.stringify(originalPhones);
        setHasHomepageChanges(hoursChanged || phonesChanged);
    }, [workingHours, phones, originalWorkingHours, originalPhones]);

    useEffect(() => {
        const loadRegions = async () => {
            try {
                const regions = await regionApi.getAllRegions();
                setAllRegions(regions);
            } catch (err) {
                console.error('Ошибка загрузки регионов:', err);
            }
        };
        loadRegions();
    }, []);

    const regions = useMemo(() => {
        const regionNames = allFarms
            .map(f => f.regionName || f.region || '')
            .filter(Boolean);
        const uniqueRegions = [...new Set(regionNames)];

        allRegions.forEach(region => {
            if (!uniqueRegions.includes(region.name)) {
                uniqueRegions.push(region.name);
            }
        });

        return uniqueRegions.sort();
    }, [allFarms, allRegions]);

    const farmNames = useMemo(() => {
        const uniqueNames = [...new Set(allFarms.map(f => f.name))];
        return uniqueNames.sort();
    }, [allFarms]);

    const loadAllFarms = async () => {
        setLoading(true);
        try {
            const data = await farmApi.getAllFarms();
            setAllFarms(data);

            if (isAdmin) {
                setFilteredFarms(data);
            } else {
                const activeFarms = data.filter(farm => farm.active === true);
                setFilteredFarms(activeFarms);
            }

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
    }, [isAdmin]);

    const handleSearch = async (region: string, name: string) => {
        const cleanRegion = region?.trim() || '';
        const cleanName = name?.trim() || '';

        if (!cleanRegion && !cleanName) {
            if (isAdmin) {
                setFilteredFarms(allFarms);
            } else {
                const activeFarms = allFarms.filter(farm => farm.active === true);
                setFilteredFarms(activeFarms);
            }
            return;
        }

        try {
            const data = await farmApi.getFarmsByFilter(
                cleanRegion || undefined,
                cleanName || undefined
            );

            if (isAdmin) {
                setFilteredFarms(data);
            } else {
                const activeFarms = data.filter(farm => farm.active === true);
                setFilteredFarms(activeFarms);
            }
        } catch (err) {
            console.error('Ошибка поиска через API:', err);

            let filtered = [...allFarms];

            if (cleanRegion) {
                filtered = filtered.filter(farm => {
                    const farmRegion = farm.regionName || farm.region || '';
                    return farmRegion.toLowerCase().includes(cleanRegion.toLowerCase());
                });
            }

            if (cleanName) {
                filtered = filtered.filter(farm =>
                    farm.name.toLowerCase().includes(cleanName.toLowerCase())
                );
            }

            if (isAdmin) {
                setFilteredFarms(filtered);
            } else {
                const activeFarms = filtered.filter(farm => farm.active === true);
                setFilteredFarms(activeFarms);
            }
        }
    };

    const handleFarmClick = (farmId: number) => {
        navigate(`/farms/${farmId}`);
    };

    const handleAddFarm = () => {
        if (!isAdmin) {
            setShowNotification({ message: 'Доступ запрещен. Только для администраторов.', type: 'error' });
            return;
        }
        navigate('/admin/farms/new');
    };

    const handleEditFarm = (farmId: number) => {
        if (!isAdmin) {
            setShowNotification({ message: 'Доступ запрещен. Только для администраторов.', type: 'error' });
            return;
        }
        navigate(`/admin/farms/${farmId}`);
    };

    const handleDeleteFarm = async (id: number, name: string) => {
        if (!isAdmin) {
            setShowNotification({ message: 'Доступ запрещен. Только для администраторов.', type: 'error' });
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
            setShowNotification({
                message: `Ферма "${showDeleteConfirm.name}" успешно удалена`,
                type: 'success'
            });
        } catch (err) {
            console.error('Ошибка удаления:', err);
            setShowNotification({
                message: 'Не удалось удалить ферму. Возможно, у неё есть активные бронирования.',
                type: 'error'
            });
        }
    };

    const handleToggleStatus = async (farm: Farm) => {
        if (!isAdmin) {
            setShowNotification({ message: 'Доступ запрещен. Только для администраторов.', type: 'error' });
            return;
        }
        try {
            await farmApi.updateFarm(farm.id, { active: !farm.active });
            await loadAllFarms();
            setShowNotification({
                message: `Ферма "${farm.name}" ${!farm.active ? 'активирована' : 'деактивирована'}`,
                type: 'success'
            });
        } catch (err) {
            console.error('Ошибка изменения статуса:', err);
            setShowNotification({ message: 'Не удалось изменить статус фермы', type: 'error' });
        }
    };

    const handleSaveHomepage = () => {
        setSavingHomepage(true);
        setTimeout(() => {
            localStorage.setItem('workingHours', JSON.stringify(workingHours));
            localStorage.setItem('phones', JSON.stringify(phones));
            setOriginalWorkingHours([...workingHours]);
            setOriginalPhones([...phones]);
            setHasHomepageChanges(false);
            setSavingHomepage(false);
            setShowNotification({ message: 'Изменения успешно сохранены!', type: 'success' });
        }, 500);
    };

    const handleCancelHomepage = () => {
        setWorkingHours([...originalWorkingHours]);
        setPhones([...originalPhones]);
        setHasHomepageChanges(false);
    };

    const closeNotification = () => {
        setShowNotification(null);
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

            <div className="homepage-hero">
                <SearchPanel
                    regions={regions}
                    farmNames={farmNames}
                    onSearch={handleSearch}
                    isAdmin={isAdmin}
                    onAddFarm={handleAddFarm}
                />
            </div>

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
                <div className="modal-overlay" onClick={() => setShowDeleteConfirm(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3>Подтверждение удаления</h3>
                            <button className="modal-close-icon" onClick={() => setShowDeleteConfirm(null)}>
                                <X size={20} strokeWidth={1.5} />
                            </button>
                        </div>
                        <div className="modal-body">
                            <p>Вы уверены, что хотите удалить ферму <strong>«{showDeleteConfirm.name}»</strong>?</p>
                            <p className="warning-text">Это действие необратимо. Все данные о ферме будут удалены.</p>
                        </div>
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

export default HomePage;