import axios from 'axios';
import type { Farm, FarmCreateDto, Image } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const client = axios.create({
    baseURL: API_BASE_URL,
    headers: { 'Content-Type': 'application/json' },
});

export const farmApi = {
    getAllFarms: async (): Promise<Farm[]> => {
        const response = await client.get('/farms');
        return response.data;
    },

    getFarmById: async (id: number): Promise<Farm> => {
        const response = await client.get(`/farms/${id}`);
        return response.data;
    },

    getFarmsByFilter: async (region?: string, name?: string): Promise<Farm[]> => {
        const params: any = {};
        if (region) params.region = region;
        if (name) params.name = name;
        const response = await client.get('/farms/filter', { params });
        return response.data;
    },

    getFarmImages: async (farmId: number): Promise<Image[]> => {
        const response = await client.get(`/farms/${farmId}/images`);
        return response.data.map((item: any) => ({
            id: item.id,
            url: item.imageUrl?.startsWith('http') ? item.imageUrl : `http://localhost:8080${item.imageUrl}`,
            isMain: item.main ?? item.isMain ?? false,
        }));
    },

    createFarm: async (data: FarmCreateDto): Promise<Farm> => {
        const response = await client.post('/farms', data);
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

    addActivityToFarm: async (farmId: number, activityName: string): Promise<void> => {
        const activity = await client.post('/activities', { name: activityName });
        await client.post(`/farms/${farmId}/activities/${activity.data.id}`);
    },

    addAccommodationToFarm: async (farmId: number, type: string, price: number): Promise<void> => {
        await client.post('/accommodations', { type, price, farmId });
    },

    removeActivityFromFarm: async (farmId: number, activityId: number): Promise<void> => {
        await client.delete(`/farms/${farmId}/activities/${activityId}`);
    },

    deleteAccommodation: async (id: number): Promise<void> => {
        await client.delete(`/accommodations/${id}`);
    },

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
};