import { useState } from 'react';
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
    const [selectedName, setSelectedName] = useState('');

    const handleSearch = () => {
        onSearch(selectedRegion, selectedName);
    };

    const handleReset = () => {
        setSelectedRegion('');
        setSelectedName('');
        onSearch('', '');
    };

    return (
        <div className="search-panel">
            <div className="container">
                <div className="search-fields">
                    <div className="search-field">
                        <label className="search-label">Регион</label>
                        <div className="search-select-wrapper">
                            <select
                                className="search-select"
                                value={selectedRegion}
                                onChange={(e) => setSelectedRegion(e.target.value)}
                            >
                                <option value="">Все</option>
                                {regions.map(region => (
                                    <option key={region} value={region}>{region}</option>
                                ))}
                            </select>
                            <span className="select-arrow">▼</span>
                        </div>
                    </div>

                    <div className="search-field">
                        <label className="search-label">Название фермы</label>
                        <div className="search-select-wrapper">
                            <select
                                className="search-select"
                                value={selectedName}
                                onChange={(e) => setSelectedName(e.target.value)}
                            >
                                <option value="">Все</option>
                                {farmNames.map(name => (
                                    <option key={name} value={name}>{name}</option>
                                ))}
                            </select>
                            <span className="select-arrow">▼</span>
                        </div>
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