import { App } from '@capacitor/app';
import { AuthService } from 'ionic-appauth';
import { CapacitorBrowser, CapacitorSecureStorage } from 'ionic-appauth/lib/capacitor';
import { isPlatform } from '@ionic/vue';
import { AxiosRequestor } from './AxiosService';
import { CapacitorRequestor } from './CapacitorService';

export class Auth {

  private static authService: AuthService | undefined;

  private static buildAuthInstance() {
    const requestor = isPlatform('ios') ? new CapacitorRequestor() : new AxiosRequestor();
    
    const authService = new AuthService(new CapacitorBrowser(), new CapacitorSecureStorage(), requestor);
    authService.authConfig = {
      client_id: 'c_104137f7-0049-40ae-811e-ad33eb59fd36',
      server_host: 'https://aac.platform.smartcommunitylab.it',
      redirect_url: isPlatform('capacitor') ? 'it.smartcommunitylab.climb.driverapp://callback' : window.location.origin + '/authcallback',
      end_session_redirect_url: isPlatform('capacitor') ? 'it.smartcommunitylab.climb.driverapp://endSession' : window.location.origin + '/endsession',
      scopes: 'openid email profile offline_access',
      pkce: true
  
    }

    if (isPlatform('capacitor')) {
      App.addListener('appUrlOpen', (data: any) => {
        if ((data.url).indexOf(authService.authConfig.redirect_url) === 0) {
          authService.authorizationCallback(data.url);
        } else {
          authService.endSessionCallback();
        }
      });
    }

    authService.init();
    return authService;
  }
  public static  async authHeader() {
    if (this.authService ) {
      const token = (await this.authService.getValidToken()).accessToken;
      return { Authorization: 'Bearer ' + token};
    } else {
      return {};
    }
  }
  public static get Instance(): AuthService {
    if (!this.authService) {
      this.authService = this.buildAuthInstance();
    }

    return this.authService;
  }
}
