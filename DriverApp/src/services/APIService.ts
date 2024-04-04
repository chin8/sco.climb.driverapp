import { Auth } from '@/services/AuthService';
import axios from "axios";

const addEvents = (routeId: string, events: object) => {
    // get token
    Auth.Instance.token$.subscribe((token) => {
        const tokenLocal = JSON.stringify(token?.accessToken);
        // send data
        axios.post(import.meta.env.VITE_SERVER_URL+'/event/TEST/' + routeId, events, {
            headers: {
                'Authorization': 'Bearer ' + tokenLocal,
                // 'appId': Config.APPID,
            }
        })
        .then((res) => console.log(res.data))
        .catch((err) => console.error(err));
    });
}


export { addEvents };