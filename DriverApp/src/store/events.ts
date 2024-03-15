import axios from "axios";
import { defineStore } from "pinia";

var AE = {
  NODE_IN_RANGE: 101,
  NODE_CHECKIN: 102,
  NODE_CHECKOUT: 103,
  NODE_AT_DESTINATION: 104,
  NODE_OUT_OF_RANGE: 105,
  STOP_LEAVING: 202,
  SET_DRIVER: 301,
  SET_HELPER: 302,
  DRIVER_POSITION: 303,
  START_ROUTE: 401,
  END_ROUTE: 402,
  BATTERY_STATE: 501,
};

interface State {
  events: [];
}

export const useEventsStore = defineStore({
  id: "event",
  state: () => ({
    events: [],
  }),
  actions: {
    setDriver(driver: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.SET_DRIVER,
        timestamp: Date.now(),
        payload: {
          volunteerId: driver,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
    setHelper(helper: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.SET_HELPER,
        timestamp: Date.now(),
        payload: {
          volunteerId: helper,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
    startRoute(stopId: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.START_ROUTE,
        timestamp: Date.now(),
        payload: {
          stopId: stopId,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
    stopLeave(stopId: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.STOP_LEAVING,
        timestamp: Date.now(),
        payload: {
          stopId: stopId,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
    nodeCheckin(passengerId: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.NODE_CHECKIN,
        timestamp: Date.now(),
        payload: {
          passengerId: passengerId,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
    nodeCheckout(passengerId: string, routeId: string) {
      const event = {
        routeId: routeId,
        wsnNodeId: null,
        eventType: AE.NODE_CHECKOUT,
        timestamp: Date.now(),
        payload: {
          passengerId: passengerId,
        },
      };
      // Geolocalizzare l'evento
      this.events.push(event);
    },
  },
});
