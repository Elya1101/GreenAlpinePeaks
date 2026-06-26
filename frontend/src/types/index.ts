export interface Region {
    id: number;
    name: string;
}

export interface AccommodationTypeInterface {
    id: number;
    name: string;
    code: string;
}

export type AccommodationTypeLegacy =
    | 'AGRITOURISM_ROOM'
    | 'DAIRY_GUEST_ROOM'
    | 'ALPINE_HUT'
    | 'APARTMENT'
    | 'TENT'
    | 'HOUSE'
    | 'LODGE';

export interface Activity {
    id: number;
    name: string;
}

export interface Accommodation {
    id: number;
    typeId: number;
    typeName?: string;
    type?: string;
    price: number;
}

export interface Image {
    id: number;
    url: string;
    isMain: boolean;
}

export interface Farm {
    id: number;
    name: string;
    regionId: number;
    regionName?: string;
    region?: string;
    active: boolean;
    description?: string;
    establishedYear?: number;
    phone?: string;
    email?: string;
    ownerId?: number;
    activities: Activity[];
    accommodations: Accommodation[];
}

export interface FarmCreateDto {
    name: string;
    regionId?: number;
    region?: string;
    active?: boolean;
    description?: string;
    email?: string;
    phone?: string;
    establishedYear?: number | null;
}


/** @deprecated Используйте Farm */
export type FarmLegacy = Farm;

/** @deprecated Используйте AccommodationTypeInterface */
export type AccommodationType = AccommodationTypeInterface;