import axios from 'axios'
import Utils from "../tools/Utils";
import {alertActions, store} from "../tools/Rdx";
const API_URL = 'http://localhost:8081/api/v1'
const AUTH_URL = 'http://localhost:8081/auth'

class BackendService {
constructor() {
    axios.interceptors.request.use(
      config => {
        store.dispatch(alertActions.clear());
        let token = Utils.getToken();
        if (token) {
          config.headers.Authorization = token;
        }
        return config;
      },
      error => {
        this.showError(error.message);
        return Promise.reject(error);
      }
    );

    axios.interceptors.response.use(
      undefined,
      error => {
        if (error.response && error.response.status && [401, 403].indexOf(error.response.status) !== -1) {
          this.showError("Ошибка авторизации");
        } else if (error.response && error.response.data && error.response.data.message) {
          this.showError(error.response.data.message);
        } else {
          this.showError(error.message);
        }
        return Promise.reject(error);
      }
    );
  }
    login(login, password) {
        return axios.post(`${AUTH_URL}/login`, {login, password})
    }

    logout() {
        return axios.get(`${AUTH_URL}/logout`, { headers : {Authorization : Utils.getToken()}})
    }


    showError = (msg) =>
    {
        store.dispatch(alertActions.error(msg))
    }
}

export default new BackendService();