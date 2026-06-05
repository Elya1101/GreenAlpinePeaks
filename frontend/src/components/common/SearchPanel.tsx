// src/components/common/SearchPanel.tsx
import { useState, useEffect, useRef } from 'react';
import { ChevronDown } from 'lucide-react';
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
        if (useCustom) setTimeout(() => regionInputRef.current?.focus(), 0);
        else setCustomRegion('');
    };

    const handleNameTypeChange = (useCustom: boolean) => {
        setIsNameCustom(useCustom);
        if (useCustom) setTimeout(() => nameInputRef.current?.focus(), 0);
        else setCustomName('');
    };

    return (
        <div className="search-panel">
            <div className="container">
                <div className="search-fields">
                    <div className="search-field">
                        <label className="search-label">Регион</label>
                        <div className="search-type-toggle">
                            <button className={`type-toggle-btn ${!isRegionCustom ? 'active' : ''}`} onClick={() => handleRegionTypeChange(false)}>Выбрать</button>
                            <button className={`type-toggle-btn ${isRegionCustom ? 'active' : ''}`} onClick={() => handleRegionTypeChange(true)}>Ввести</button>
                        </div>
                        {!isRegionCustom ? (
                            <div className="search-select-wrapper">
                                <select className="search-select" value={selectedRegion} onChange={(e) => setSelectedRegion(e.target.value)}>
                                    <option value="">Все регионы</option>
                                    {regions.map(region => <option key={region} value={region}>{region}</option>)}
                                </select>
                                <ChevronDown size={16} className="select-arrow" />
                            </div>
                        ) : (
                            <input ref={regionInputRef} type="text" className="search-input" placeholder="Введите регион..." value={customRegion} onChange={(e) => setCustomRegion(e.target.value)} onKeyPress={(e) => e.key === 'Enter' && handleSearch()} />
                        )}
                    </div>

                    <div className="search-field">
                        <label className="search-label">Название фермы</label>
                        <div className="search-type-toggle">
                            <button className={`type-toggle-btn ${!isNameCustom ? 'active' : ''}`} onClick={() => handleNameTypeChange(false)}>Выбрать</button>
                            <button className={`type-toggle-btn ${isNameCustom ? 'active' : ''}`} onClick={() => handleNameTypeChange(true)}>Ввести</button>
                        </div>
                        {!isNameCustom ? (
                            <div className="search-select-wrapper">
                                <select className="search-select" value={selectedName} onChange={(e) => setSelectedName(e.target.value)}>
                                    <option value="">Все фермы</option>
                                    {farmNames.map(name => <option key={name} value={name}>{name}</option>)}
                                </select>
                                <ChevronDown size={16} className="select-arrow" />
                            </div>
                        ) : (
                            <input ref={nameInputRef} type="text" className="search-input" placeholder="Введите название..." value={customName} onChange={(e) => setCustomName(e.target.value)} onKeyPress={(e) => e.key === 'Enter' && handleSearch()} />
                        )}
                    </div>

                    {isAdmin && onAddFarm && <button className="add-farm-button" onClick={onAddFarm}>+ Добавить новую ферму</button>}
                </div>

                <div className="search-actions">
                    <button className="btn-primary" onClick={handleSearch}>Показать</button>
                    <button className="btn-secondary" onClick={handleReset}>Сбросить</button>
                </div>
            </div>
        </div>
    );
};

export default SearchPanel;