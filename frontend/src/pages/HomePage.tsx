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
    const [viewMode, setViewMode] = useState<'admin' | 'user'>(isAdmin ? 'admin' : 'user');
    const [showDeleteConfirm, setShowDeleteConfirm] = useState<{ id: number; name: string } | null>(null);
    const [farmImages, setFarmImages] = useState<Map<number, string>>(new Map());

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
                        imagesMap.set(farm.id, mainImage.url);
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
            // Fallback на клиентскую фильтрацию
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
        navigate('/admin/farms/new');
    };

    const handleEditFarm = (farmId: number) => {
        navigate(`/admin/farms/${farmId}`);
    };

    const handleDeleteFarm = async (id: number, name: string) => {
        setShowDeleteConfirm({ id, name });
    };

    const confirmDelete = async () => {
        if (!showDeleteConfirm) return;
        try {
            await farmApi.deleteFarm(showDeleteConfirm.id);
            await loadAllFarms();
            setShowDeleteConfirm(null);
        } catch (err) {
            console.error('Ошибка удаления:', err);
            alert('Не удалось удалить ферму');
        }
    };

    const handleToggleStatus = async (farm: Farm) => {
        try {
            await farmApi.updateFarm(farm.id, { active: !farm.active });
            await loadAllFarms();
        } catch (err) {
            console.error('Ошибка изменения статуса:', err);
            alert('Не удалось изменить статус');
        }
    };

    const handleViewModeChange = (mode: 'admin' | 'user') => {
        setViewMode(mode);
    };

    if (loading) {
        return (
            <div>
                <Header isAdmin={isAdmin} viewMode={viewMode} onViewModeChange={handleViewModeChange} />
                <div className="loading-spinner">Загрузка ферм...</div>
                <Footer onAdminLogin={onAdminLogin} onAdminLogout={onAdminLogout} isAdmin={isAdmin} />
            </div>
        );
    }

    const showAdminActions = isAdmin && viewMode === 'admin';

    return (
        <div>
            <Header isAdmin={isAdmin} viewMode={viewMode} onViewModeChange={handleViewModeChange} />
            <SearchPanel
                regions={regions}
                farmNames={farmNames}
                onSearch={handleSearch}
                isAdmin={showAdminActions}
                onAddFarm={handleAddFarm}
            />

            <div className="container">
                <div className="farms-list">
                    {filteredFarms.map(farm => (
                        <FarmCard
                            key={farm.id}
                            id={farm.id}
                            name={farm.name}
                            description={farm.description || 'Описание фермы пока не добавлено'}
                            imageUrl={farmImages.get(farm.id)}
                            isActive={farm.active}
                            isAdmin={showAdminActions}
                            onDetails={() => handleFarmClick(farm.id)}
                            onEdit={() => handleEditFarm(farm.id)}
                            onDelete={() => handleDeleteFarm(farm.id, farm.name)}
                            onToggleStatus={() => handleToggleStatus(farm)}
                        />
                    ))}
                </div>
                {filteredFarms.length === 0 && (
                    <div className="no-results">Фермы не найдены</div>
                )}
            </div>

            <Footer onAdminLogin={onAdminLogin} onAdminLogout={onAdminLogout} isAdmin={isAdmin} />

            {showDeleteConfirm && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Подтверждение удаления</h3>
                        <p>Вы уверены, что хотите удалить ферму <strong>{showDeleteConfirm.name}</strong>?</p>
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