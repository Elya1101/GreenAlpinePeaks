import axios from 'axios';
import type { Farm, FarmCreateDto, Image, Region, AccommodationType, Activity } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const client = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
});

export const regionApi = {
    getAllRegions: async (): Promise<Region[]> => {
        const response = await client.get('/regions');
        return response.data;
    },

    getRegionById: async (id: number): Promise<Region> => {
        const response = await client.get(`/regions/${id}`);
        return response.data;
    },

    getRegionByName: async (name: string): Promise<Region | null> => {
        try {
            const response = await client.get(`/regions/search`, { params: { name } });
            return response.data;
        } catch (err: any) {
            if (err.response?.status === 404) {
                return null;
            }
            throw err;
        }
    },

    createRegion: async (data: { name: string }): Promise<Region> => {
        const response = await client.post('/regions', data);
        return response.data;
    },

    updateRegion: async (id: number, data: { name: string }): Promise<Region> => {
        const response = await client.put(`/regions/${id}`, data);
        return response.data;
    },

    deleteRegion: async (id: number): Promise<void> => {
        await client.delete(`/regions/${id}`);
    },
};

export const accommodationTypeApi = {
    getAllTypes: async (): Promise<AccommodationType[]> => {
        const response = await client.get('/accommodation-types');
        return response.data;
    },

    getTypeById: async (id: number): Promise<AccommodationType> => {
        const response = await client.get(`/accommodation-types/${id}`);
        return response.data;
    },

    getTypeByName: async (name: string): Promise<AccommodationType | null> => {
        try {
            const response = await client.get(`/accommodation-types/search`, { params: { name } });
            return response.data;
        } catch (err: any) {
            if (err.response?.status === 404) {
                return null;
            }
            throw err;
        }
    },

    createType: async (data: { name: string; code: string }): Promise<AccommodationType> => {
        const response = await client.post('/accommodation-types', data);
        return response.data;
    },

    updateType: async (id: number, data: { name: string; code: string }): Promise<AccommodationType> => {
        const response = await client.put(`/accommodation-types/${id}`, data);
        return response.data;
    },

    deleteType: async (id: number): Promise<void> => {
        await client.delete(`/accommodation-types/${id}`);
    },
};

// API ДЛЯ АКТИВНОСТЕЙ
export const activitiesApi = {
    getAllActivities: async (): Promise<Activity[]> => {
        const response = await client.get('/activities');
        return response.data;
    },

    getActivityById: async (id: number): Promise<Activity> => {
        const response = await client.get(`/activities/${id}`);
        return response.data;
    },

    searchActivities: async (name: string): Promise<Activity[]> => {
        const response = await client.get(`/activities/search`, { params: { name } });
        return response.data;
    },

    createActivity: async (data: { name: string }): Promise<Activity> => {
        const response = await client.post('/activities', data);
        return response.data;
    },

    updateActivity: async (id: number, data: { name: string }): Promise<Activity> => {
        const response = await client.put(`/activities/${id}`, data);
        return response.data;
    },

    deleteActivity: async (id: number): Promise<void> => {
        await client.delete(`/activities/${id}`);
    },
};

// Расширенный тип для создания фермы с жильем
interface FarmCreateWithAccommodationsDto extends FarmCreateDto {
    accommodations?: Array<{
        typeId: number;
        price: number;
    }>;
}

export const farmApi = {
    getAllFarms: async (): Promise<Farm[]> => {
        const response = await client.get('/farms');
        return response.data.map((farm: any) => ({
            ...farm,
            region: farm.regionName || farm.region,
            regionId: farm.regionId,
            regionName: farm.regionName,
        }));
    },

    getFarmById: async (id: number): Promise<Farm> => {
        const response = await client.get(`/farms/${id}`);
        const farm = response.data;
        return {
            ...farm,
            region: farm.regionName || farm.region,
            regionId: farm.regionId,
            regionName: farm.regionName,
        };
    },

    getFarmForEdit: async (id: number): Promise<any> => {
        const response = await client.get(`/farms/${id}/edit`);
        return response.data;
    },

    getFarmsByFilter: async (region?: string, name?: string): Promise<Farm[]> => {
        const params: any = {};
        if (region) params.region = region;
        if (name) params.name = name;
        const response = await client.get('/farms/filter', { params });
        return response.data.map((farm: any) => ({
            ...farm,
            region: farm.regionName || farm.region,
            regionId: farm.regionId,
            regionName: farm.regionName,
        }));
    },

    getFarmsByRegionId: async (regionId: number): Promise<Farm[]> => {
        const response = await client.get(`/farms/by-region/${regionId}`);
        return response.data.map((farm: any) => ({
            ...farm,
            region: farm.regionName || farm.region,
            regionId: farm.regionId,
            regionName: farm.regionName,
        }));
    },

    getFarmImages: async (farmId: number): Promise<Image[]> => {
        try {
            const response = await client.get(`/farms/${farmId}/images`);
            return response.data.map((item: any) => ({
                id: item.id,
                url: item.imageUrl?.startsWith('http')
                    ? item.imageUrl
                    : `http://localhost:8080${item.imageUrl}`,
                isMain: item.main ?? item.isMain ?? false,
            }));
        } catch (err) {
            console.error('Ошибка загрузки изображений:', err);
            return [];
        }
    },

    createFarm: async (data: FarmCreateDto): Promise<Farm> => {
        const requestData: any = {
            name: data.name,
            active: data.active,
            region: data.region,
        };

        if (data.description) requestData.description = data.description;
        if (data.email) requestData.email = data.email;
        if (data.phone) requestData.phone = data.phone;
        if (data.establishedYear) requestData.establishedYear = data.establishedYear;

        const response = await client.post('/farms', requestData);
        return response.data;
    },

    // Исправленный метод с использованием расширенного типа
    createFarmWithAccommodations: async (data: FarmCreateWithAccommodationsDto): Promise<Farm> => {
        const requestData: any = {
            name: data.name,
            active: data.active,
            region: data.region,
        };

        if (data.description) requestData.description = data.description;
        if (data.email) requestData.email = data.email;
        if (data.phone) requestData.phone = data.phone;
        if (data.establishedYear) requestData.establishedYear = data.establishedYear;
        if (data.accommodations) requestData.accommodations = data.accommodations;

        const response = await client.post('/farms/with-accommodations', requestData);
        return response.data;
    },

    updateFarm: async (id: number, data: Partial<FarmCreateDto>): Promise<Farm> => {
        const cleanData: any = {};

        if (data.name !== undefined) cleanData.name = data.name;
        if (data.active !== undefined) cleanData.active = data.active;
        if (data.region !== undefined) cleanData.region = data.region;
        if (data.description !== undefined) cleanData.description = data.description;
        if (data.email !== undefined) cleanData.email = data.email;
        if (data.phone !== undefined) cleanData.phone = data.phone;
        if (data.establishedYear !== undefined) cleanData.establishedYear = data.establishedYear;

        const response = await client.put(`/farms/${id}`, cleanData);
        return response.data;
    },

    deleteFarm: async (id: number): Promise<void> => {
        await client.delete(`/farms/${id}`);
    },

    addActivityToFarm: async (farmId: number, activityIdOrName: number | string): Promise<void> => {
        let activityId: number;

        if (typeof activityIdOrName === 'number') {
            activityId = activityIdOrName;
        } else {
            try {
                const activity = await client.post('/activities', { name: activityIdOrName });
                activityId = activity.data.id;
            } catch (err: any) {
                if (err.response?.status === 409) {
                    const searchResponse = await client.get('/activities/search', {
                        params: { name: activityIdOrName }
                    });

                    const data = searchResponse.data;
                    if (Array.isArray(data) && data.length > 0) {
                        activityId = data[0].id;
                    } else if (data && typeof data === 'object' && data.id) {
                        activityId = data.id;
                    } else {
                        throw new Error(`Не удалось найти или создать активность "${activityIdOrName}"`);
                    }
                } else {
                    throw err;
                }
            }
        }

        await client.post(`/farms/${farmId}/activities/${activityId}`);
    },

    removeActivityFromFarm: async (farmId: number, activityId: number): Promise<void> => {
        await client.delete(`/farms/${farmId}/activities/${activityId}`);
    },

    // ========== ЭНДПОИНТЫ ДЛЯ ЖИЛЬЯ - ИСПРАВЛЕННАЯ ВЕРСИЯ ==========
    addAccommodationToFarm: async (farmId: number, typeId: number, price: number): Promise<void> => {
        console.log('Добавление жилья (исправленная версия):', { farmId, typeId, price });

        await client.post('/accommodations', {
            typeId: typeId,
            price: price,
            farmId: farmId
        });
    },

    deleteAccommodation: async (id: number): Promise<void> => {
        await client.delete(`/accommodations/${id}`);
    },

    updateAccommodation: async (id: number, price: number): Promise<void> => {
        await client.put(`/accommodations/${id}`, { price });
    },
    // ================================================================

    uploadImage: async (farmId: number, file: File, isMain: boolean): Promise<void> => {
        const formData = new FormData();
        formData.append('image', file);
        formData.append('isMain', String(isMain));
        await client.post(`/farms/${farmId}/images`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    deleteImage: async (farmId: number, imageId: number): Promise<void> => {
        await client.delete(`/farms/${farmId}/images/${imageId}`);
    },

    setMainImage: async (farmId: number, imageId: number): Promise<void> => {
        await client.patch(`/farms/${farmId}/images/${imageId}/main`);
    },

    getCacheSize: async (): Promise<number> => {
        const response = await client.get('/farms/cache-size');
        return response.data;
    },
};

export const api = {
    farms: farmApi,
    regions: regionApi,
    accommodationTypes: accommodationTypeApi,
    activities: activitiesApi,
};

export default client;