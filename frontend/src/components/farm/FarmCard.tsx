import './FarmCard.css';

interface FarmCardProps {
    id: number;
    name: string;
    description: string;
    imageUrl?: string;
    isActive?: boolean;
    isAdmin?: boolean;
    onEdit?: () => void;
    onDelete?: () => void;
    onToggleStatus?: () => void;
    onDetails?: () => void;
}

const FarmCard = ({
                      name,
                      description,
                      imageUrl,
                      isActive = true,
                      isAdmin = false,
                      onEdit,
                      onDelete,
                      onToggleStatus,
                      onDetails
                  }: FarmCardProps) => {
    return (
        <div className={`farm-card ${!isActive ? 'farm-card-inactive' : ''}`}>
            <div className="farm-card-image">
                {imageUrl ? (
                    <img src={imageUrl} alt={name} />
                ) : (
                    <div className="farm-card-placeholder">🏔️</div>
                )}
            </div>

            <div className="farm-card-content">
                <h3 className="farm-card-title">{name}</h3>
                <p className="farm-card-description">{description}</p>

                {!isAdmin && (
                    <button className="farm-card-button" onClick={onDetails}>
                        Подробнее
                    </button>
                )}
            </div>

            {isAdmin && (
                <div className="farm-card-actions">
                    <button className="farm-card-edit" onClick={onEdit} title="Редактировать">
                        ✏️
                    </button>
                    <button className="farm-card-delete" onClick={onDelete} title="Удалить">
                        🗑️
                    </button>
                    <label className="toggle-switch">
                        <input
                            type="checkbox"
                            checked={isActive}
                            onChange={onToggleStatus}
                        />
                        <span className="toggle-slider"></span>
                    </label>
                </div>
            )}
        </div>
    );
};

export default FarmCard;