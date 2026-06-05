import { useState, useEffect, useRef } from 'react';
import './SearchPanel.css';

interface SearchPanelProps {
    regions: string[];
    farmNames: string[];
    onSearch: (region: string, name: string) => void;
    isAdmin?: boolean;
    onAddFarm?: () => void;
}

const SearchPanel = ({ regions, farmNames, onSearch, isAdmin = false, onAddFarm }: SearchPanelProps) => {
    const [selectedRegion, setSelectedRegion] = useState('');
    const [customRegion, setCustomRegion] = useState('');
    const [selectedName, setSelectedName] = useState('');
    const [customName, setCustomName] = useState('');
    const [isRegionCustom, setIsRegionCustom] = useState(false);
    const [isNameCustom, setIsNameCustom] = useState(false);

    const regionInputRef = useRef<HTMLInputElement>(null);
    const nameInputRef = useRef<HTMLInputElement>(null);

    // Объединённые списки для выпадающих списков
    const allRegions = [...regions];
    const allFarmNames = [...farmNames];

    const handleSearch = () => {
        const regionValue = isRegionCustom ? customRegion : selectedRegion;
        const nameValue = isNameCustom ? customName : selectedName;
        onSearch(regionValue, nameValue);
    };

    const handleReset = () => {
        setSelectedRegion('');
        setCustomRegion('');
        setIsRegionCustom(false);
        setSelectedName('');
        setCustomName('');
        setIsNameCustom(false);
        onSearch('', '');
    };

    const handleRegionTypeChange = (useCustom: boolean) => {
        setIsRegionCustom(useCustom);
        if (useCustom) {
            setSelectedRegion('');
            setTimeout(() => regionInputRef.current?.focus(), 0);
        } else {
            setCustomRegion('');
        }
    };

    const handleNameTypeChange = (useCustom: boolean) => {
        setIsNameCustom(useCustom);
        if (useCustom) {
            setSelectedName('');
            setTimeout(() => nameInputRef.current?.focus(), 0);
        } else {
            setCustomName('');
        }
    };

    return (
        <div className="search-panel">
            <div className="container">
                <div className="search-fields">
                    {/* Поле региона */}
                    <div className="search-field">
                        <label className="search-label">Регион</label>
                        <div className="search-type-toggle">
                            <button
                                className={`type-toggle-btn ${!isRegionCustom ? 'active' : ''}`}
                                onClick={() => handleRegionTypeChange(false)}
                                type="button"
                            >
                                📋 Выбрать
                            </button>
                            <button
                                className={`type-toggle-btn ${isRegionCustom ? 'active' : ''}`}
                                onClick={() => handleRegionTypeChange(true)}
                                type="button"
                            >
                                ✏️ Ввести
                            </button>
                        </div>

                        {!isRegionCustom ? (
                            <div className="search-select-wrapper">
                                <select
                                    className="search-select"
                                    value={selectedRegion}
                                    onChange={(e) => setSelectedRegion(e.target.value)}
                                >
                                    <option value="">Все регионы</option>
                                    {allRegions.map(region => (
                                        <option key={region} value={region}>{region}</option>
                                    ))}
                                </select>
                                <span className="select-arrow">▼</span>
                            </div>
                        ) : (
                            <input
                                ref={regionInputRef}
                                type="text"
                                className="search-input"
                                placeholder="Введите регион..."
                                value={customRegion}
                                onChange={(e) => setCustomRegion(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                            />
                        )}
                    </div>

                    {/* Поле названия фермы */}
                    <div className="search-field">
                        <label className="search-label">Название фермы</label>
                        <div className="search-type-toggle">
                            <button
                                className={`type-toggle-btn ${!isNameCustom ? 'active' : ''}`}
                                onClick={() => handleNameTypeChange(false)}
                                type="button"
                            >
                                📋 Выбрать
                            </button>
                            <button
                                className={`type-toggle-btn ${isNameCustom ? 'active' : ''}`}
                                onClick={() => handleNameTypeChange(true)}
                                type="button"
                            >
                                ✏️ Ввести
                            </button>
                        </div>

                        {!isNameCustom ? (
                            <div className="search-select-wrapper">
                                <select
                                    className="search-select"
                                    value={selectedName}
                                    onChange={(e) => setSelectedName(e.target.value)}
                                >
                                    <option value="">Все фермы</option>
                                    {allFarmNames.map(name => (
                                        <option key={name} value={name}>{name}</option>
                                    ))}
                                </select>
                                <span className="select-arrow">▼</span>
                            </div>
                        ) : (
                            <input
                                ref={nameInputRef}
                                type="text"
                                className="search-input"
                                placeholder="Введите название фермы..."
                                value={customName}
                                onChange={(e) => setCustomName(e.target.value)}
                                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                            />
                        )}
                    </div>

                    {isAdmin && onAddFarm && (
                        <button className="add-farm-button" onClick={onAddFarm}>
                            + Добавить новую ферму
                        </button>
                    )}
                </div>

                <div className="search-actions">
                    <button className="btn-primary" onClick={handleSearch}>
                        Показать
                    </button>
                    <button className="btn-secondary" onClick={handleReset}>
                        Сбросить
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SearchPanel;