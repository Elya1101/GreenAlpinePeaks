import { useNavigate } from 'react-router-dom';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import './NotFoundPage.css';

const NotFoundPage = () => {
    const navigate = useNavigate();

    return (
        <div>
            <Header />
            <div className="not-found-container">
                <div className="not-found-content">
                    <h1 className="not-found-code">404</h1>
                    <h2 className="not-found-title">Страница не найдена</h2>
                    <p className="not-found-text">
                        Извините, страница, которую вы ищете, не существует или была перемещена.
                    </p>
                    <button className="not-found-button" onClick={() => navigate('/')}>
                        Вернуться на главную
                    </button>
                </div>
            </div>
            <Footer />
        </div>
    );
};

export default NotFoundPage;