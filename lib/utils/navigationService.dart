import 'package:flutter/material.dart';

class NavigationService {
  late GlobalKey<NavigatorState> navigationKey;

  static NavigationService instance = NavigationService();

  NavigationService() {
    navigationKey = GlobalKey<NavigatorState>();
  }

  Future<dynamic> navigateToReplacement(String rn, String argument) async {
    var result = await navigationKey.currentState!.pushReplacementNamed(rn);
    return result;
  }

  Future<dynamic> navigateTo(String rn, String argument) async {
    Object? result;
    String? currentRoute = ModalRoute.of(navigationKey.currentContext!)!.settings.name;
    if (currentRoute != 'reCaptcha') {
      result = await navigationKey.currentState!.pushNamed(rn, arguments: argument);
    }
    return result;
  }

  Future<dynamic> navigateToRoute(MaterialPageRoute rn) async {
    var result = await navigationKey.currentState!.push(rn);
    return result;
  }

  goback() {
    return navigationKey.currentState!.pop();
  }
}
