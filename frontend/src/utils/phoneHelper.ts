// src/utils/phoneHelper.ts

/**
 * Очищает номер телефона от всех нецифровых символов, кроме +
 */
export const cleanPhoneNumber = (phone: string): string => {
    if (!phone) return '';

    // Сохраняем + в начале, если он есть, и все цифры
    let hasPlus = phone.trim().startsWith('+');
    let digits = phone.replace(/[^\d]/g, '');

    // Если был + в начале, добавляем его обратно
    if (hasPlus && digits.length > 0) {
        return '+' + digits;
    }

    return digits;
};

/**
 * Проверяет, является ли номер телефона валидным (7-20 цифр)
 */
export const isValidPhoneNumber = (phone: string): boolean => {
    if (!phone) return true; // Пустое поле допустимо

    const cleaned = cleanPhoneNumber(phone);
    // Убираем + для подсчета цифр
    const digits = cleaned.replace(/^\+/, '');

    return digits.length >= 7 && digits.length <= 20;
};

/**
 * Форматирует номер телефона для отображения (читаемый вид)
 */
export const formatPhoneForDisplay = (phone: string): string => {
    if (!phone) return '';

    const cleaned = cleanPhoneNumber(phone);

    // Если начинается с +375 (Беларусь)
    if (cleaned.startsWith('+375') && cleaned.length === 13) {
        const code = cleaned.slice(0, 4); // +375
        const operator = cleaned.slice(4, 6); // 29, 44, 33 и т.д.
        const part1 = cleaned.slice(6, 9);
        const part2 = cleaned.slice(9, 11);
        const part3 = cleaned.slice(11, 13);
        return `${code} (${operator}) ${part1}-${part2}-${part3}`;
    }

    // Если начинается с +7 (Россия/Казахстан)
    if (cleaned.startsWith('+7') && cleaned.length === 12) {
        const code = cleaned.slice(0, 2); // +7
        const area = cleaned.slice(2, 5);
        const part1 = cleaned.slice(5, 8);
        const part2 = cleaned.slice(8, 10);
        const part3 = cleaned.slice(10, 12);
        return `${code} ${area} ${part1}-${part2}-${part3}`;
    }

    // Если начинается с + (другие страны)
    if (cleaned.startsWith('+')) {
        const code = cleaned.slice(0, 3);
        const rest = cleaned.slice(3);
        if (rest.length === 9) {
            return `${code} ${rest.slice(0, 3)} ${rest.slice(3, 6)} ${rest.slice(6, 9)}`;
        }
        if (rest.length === 10) {
            return `${code} ${rest.slice(0, 3)} ${rest.slice(3, 6)} ${rest.slice(6, 10)}`;
        }
        return cleaned;
    }

    // Для номеров без кода страны
    if (cleaned.length === 9) {
        return `${cleaned.slice(0, 3)} ${cleaned.slice(3, 6)} ${cleaned.slice(6, 9)}`;
    }

    return cleaned;
};

/**
 * Получает сообщение об ошибке для телефона
 */
export const getPhoneErrorMessage = (phone: string): string | null => {
    if (!phone) return null;

    const cleaned = cleanPhoneNumber(phone);
    const digits = cleaned.replace(/^\+/, '');

    if (digits.length < 7) {
        return `Номер телефона слишком короткий (${digits.length} цифр). Должно быть минимум 7 цифр.`;
    }

    if (digits.length > 20) {
        return `Номер телефона слишком длинный (${digits.length} цифр). Максимум 20 цифр.`;
    }

    return null;
};