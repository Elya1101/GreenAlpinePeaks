import axios from 'axios';
import type { Farm, FarmCreateDto, Image } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

// Публичный клиент - для всех GET запросов (без заголовков)
const publicClient = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
});

// Админский клиент - только для POST/PUT/DELETE
const adminClient = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
});

// Добавляем X-User-Id только для админских операций
adminClient.interceptors.request.use((config) => {
    // Для админских операций используем фиксированный ID
    config.headers['X-User-Id'] = '1';
    return config;
});

export const farmApi = {
    // Публичные методы (без авторизации)
    getAllFarms: async (): Promise<Farm[]> => {
        const response = await publicClient.get('/farms');
        return response.data;
    },

    getFarmById: async (id: number): Promise<Farm> => {
        const response = await publicClient.get(`/farms/${id}`);
        return response.data;
    },

    getFarmsByRegion: async (region: string): Promise<Farm[]> => {
        const response = await publicClient.get('/farms/filter', { params: { region } });
        return response.data;
    },

    getFarmsByName: async (name: string): Promise<Farm[]> => {
        const response = await publicClient.get('/farms/search/by-name-native', { params: { name } });
        return response.data;
    },

    getFarmImages: async (farmId: number): Promise<Image[]> => {
        const response = await publicClient.get(`/farms/${farmId}/images`);
        return response.data.map((item: any) => ({
            id: item.id,
            url: item.imageUrl?.startsWith('http') ? item.imageUrl : `http://localhost:8080${item.imageUrl}`,
            isMain: item.main ?? item.isMain ?? false,
        }));
    },

    // Админские методы (требуют авторизации)
    createFarm: async (data: FarmCreateDto): Promise<Farm> => {
        const response = await adminClient.post('/farms', data);
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

        const response = await adminClient.put(`/farms/${id}`, cleanData);
        return response.data;
    },

    deleteFarm: async (id: number): Promise<void> => {
        await adminClient.delete(`/farms/${id}`);
    },

    addActivityToFarm: async (farmId: number, activityName: string): Promise<void> => {
        const activity = await adminClient.post('/activities', { name: activityName });
        await adminClient.post(`/farms/${farmId}/activities/${activity.data.id}`);
    },

    addAccommodationToFarm: async (farmId: number, type: string, price: number): Promise<void> => {
        await adminClient.post('/accommodations', { type, price, farmId });
    },

    removeActivityFromFarm: async (farmId: number, activityId: number): Promise<void> => {
        await adminClient.delete(`/farms/${farmId}/activities/${activityId}`);
    },

    deleteAccommodation: async (id: number): Promise<void> => {
        await adminClient.delete(`/accommodations/${id}`);
    },

    uploadImage: async (farmId: number, file: File, isMain: boolean): Promise<void> => {
        const formData = new FormData();
        formData.append('image', file);
        formData.append('isMain', String(isMain));
        await adminClient.post(`/farms/${farmId}/images`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    deleteImage: async (farmId: number, imageId: number): Promise<void> => {
        await adminClient.delete(`/farms/${farmId}/images/${imageId}`);
    },

    setMainImage: async (farmId: number, imageId: number): Promise<void> => {
        await adminClient.patch(`/farms/${farmId}/images/${imageId}/main`);
    },
};