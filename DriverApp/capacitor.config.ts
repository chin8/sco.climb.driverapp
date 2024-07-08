import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'it.smartcommunitylab.climb.driverapp',
  appName: 'driverApp',
  webDir: 'dist',
  server: {
    androidScheme: 'http'
  }
};

export default config;
