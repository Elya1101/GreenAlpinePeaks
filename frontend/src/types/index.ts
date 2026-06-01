export interface Farm {
    id: number;
    name: string;
    region: string;
    active: boolean;
    description?: string;
    email?: string;
    phone?: string;
    establishedYear?: number;
    ownerId?: number;
    activities: Activity[];
    accommodations: Accommodation[];
}

export interface Activity {
    id: number;
    name: string;
}

export interface Accommodation {
    id: number;
    type: string;
    price: number;
}

export interface Image {
    id: number;
    url: string;
    isMain: boolean;
}

export interface FarmCreateDto {
    name: string;
    region: string;
    active: boolean;
    description?: string;
    email?: string;
    phone?: string;
    establishedYear?: number | null;
}

export type AccommodationType =
    | 'AGRITOURISM_ROOM'
    | 'DAIRY_GUEST_ROOM'
    | 'ALPINE_HUT'
    | 'APARTMENT'
    | 'TENT'
    | 'HOUSE'
    | 'LODGE';