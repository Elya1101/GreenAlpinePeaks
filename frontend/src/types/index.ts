// ==================== Базовые сущности ====================

export interface Region {
    id: number;
    name: string;
}

export interface AccommodationTypeInterface {
    id: number;
    name: string;
    code: string;
}

// Legacy тип для обратной совместимости
export type AccommodationTypeLegacy =
    | 'AGRITOURISM_ROOM'
    | 'DAIRY_GUEST_ROOM'
    | 'ALPINE_HUT'
    | 'APARTMENT'
    | 'TENT'
    | 'HOUSE'
    | 'LODGE';

// ==================== Активности и жильё ====================

export interface Activity {
    id: number;
    name: string;
}

export interface Accommodation {
    id: number;
    typeId: number;
    typeName?: string;
    type?: string;  // для обратной совместимости
    price: number;
}

// ==================== Изображения ====================

export interface Image {
    id: number;
    url: string;
    isMain: boolean;
}

// ==================== Ферма (основная) ====================

export interface Farm {
    id: number;
    name: string;
    regionId: number;
    regionName?: string;
    region?: string;      // для обратной совместимости
    active: boolean;
    description?: string;
    establishedYear?: number;
    phone?: string;
    email?: string;
    ownerId?: number;
    activities: Activity[];
    accommodations: Accommodation[];
}

// ==================== DTO для создания/обновления ====================

export interface FarmCreateDto {
    name: string;
    regionId?: number;
    region?: string;      // для обратной совместимости
    active?: boolean;
    description?: string;
    email?: string;
    phone?: string;
    establishedYear?: number | null;
}

// ==================== Для совместимости со старым кодом ====================

/** @deprecated Используйте Farm */
export type FarmLegacy = Farm;

/** @deprecated Используйте AccommodationTypeInterface */
export type AccommodationType = AccommodationTypeInterface;