import { Auth } from '@/services/AuthService';
import axios from "axios";

const addEvents = (routeId: string, events: object) => {
    return new Promise((resolve, reject) => {
        // get token
        Auth.Instance.token$.subscribe((token) => {
            const tokenLocal = JSON.stringify(token?.accessToken);
            // send data
            axios.post(import.meta.env.VITE_SERVER_URL + '/event/TEST/' + routeId, events, {
                headers: {
                    'Authorization': 'Bearer ' + tokenLocal,
                    // 'appId': Config.APPID,
                }
            })
            .then((res) => {
                if (res.status === 200) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            })
            .catch((err) => {
                console.error(err);
                reject(err);
            });
        });
    });
}


export { addEvents };