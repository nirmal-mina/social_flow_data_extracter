import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';
import 'package:social_flow_data_extracter/newpipeextractor_dart.dart';
import 'package:social_flow_data_extracter/utils/httpClient.dart';
import 'package:social_flow_data_extracter/utils/navigationService.dart';

bool resolvingCaptcha = false;

class ReCaptchaPage extends StatefulWidget {
  const ReCaptchaPage({Key? key}) : super(key: key);

  static Future<dynamic> checkInfo(
      dynamic info, Future<dynamic> Function() task) async {
    if ((info as Map).containsKey("error")) {
      if (info["error"].contains("reCaptcha")) {
        if (!resolvingCaptcha) {
          resolvingCaptcha = true;
          String url = info["error"].split(":").last.trim();
          await NavigationService.instance.navigateTo("reCaptcha", "http:$url");
          var newInfo = await task();
          resolvingCaptcha = false;
          return newInfo;
        }
      }
    } else {
      return info;
    }
  }

  @override
  State<ReCaptchaPage> createState() => _ReCaptchaPageState();
}

class _ReCaptchaPageState extends State<ReCaptchaPage> {
  InAppWebViewController? controller;
  String foundCookies = "";

  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)?.settings.arguments;
    String url = args is String ? args : '';
    return Material(
      child: Scaffold(
        appBar: AppBar(
          elevation: 0,
          title: const ListTile(
            title: Text("reCaptcha", style: TextStyle(color: Colors.white)),
            subtitle: Text("Solve the reCaptcha and confirm",
                style: TextStyle(color: Color.fromRGBO(255, 255, 255, 0.6))),
          ),
          backgroundColor: Colors.redAccent,
          actions: [
            IconButton(
              icon: const Icon(Icons.check_rounded),
              color: Colors.white,
              onPressed: () async {
                // controller.getUrl() returns a WebUri? in newer flutter_inappwebview
                final navigator = Navigator.of(context);
                WebUri? currentWebUri = await controller?.getUrl();
                String currentUrl = currentWebUri?.toString() ?? url;

                var info = await NewPipeExtractorDart.extractorChannel
                    .invokeMethod('getCookieByUrl', {"url": currentUrl});
                if (!mounted) return;
                String? cookies = info['cookie'];
                handleCookies(cookies);

                // Sometimes cookies are inside the url
                final int abuseStart = currentUrl.indexOf("google_abuse=");
                if (abuseStart != -1) {
                  final int abuseEnd = currentUrl.indexOf("+path");
                  try {
                    String abuseCookie =
                        currentUrl.substring(abuseStart + 12, abuseEnd);
                    var decoded = await NewPipeExtractorDart.extractorChannel
                        .invokeMethod('decodeCookie', {"cookie": abuseCookie});
                    if (!mounted) return;
                    if (decoded is String) {
                      handleCookies(decoded);
                    }
                  } catch (_) {}
                }
                await NewPipeExtractorDart.extractorChannel
                    .invokeMethod('setCookie', {"cookie": foundCookies});
                if (!mounted) return;
                navigator.pop();
              },
            ),
          ],
        ),
        body: InAppWebView(
          initialUrlRequest: URLRequest(
              url: WebUri(url), headers: ExtractorHttpClient.defaultHeaders),
          onLoadStop: (cont, _) {
            controller = cont;
          },
        ),
      ),
    );
  }

  void handleCookies(String? cookies) {
    if (cookies == null) {
      return;
    }
    if (cookies.contains("s_gl=") ||
        cookies.contains("goojf=") ||
        cookies.contains("VISITOR_INFO1_LIVE=") ||
        cookies.contains("GOOGLE_ABUSE_EXEMPTION=")) {
      if (foundCookies.contains(cookies)) {
        return;
      }
      if (foundCookies.isEmpty || foundCookies.endsWith("; ")) {
        foundCookies += cookies;
      } else if (foundCookies.endsWith(";")) {
        foundCookies += " $cookies";
      } else {
        foundCookies += "; $cookies";
      }
    }
  }
}
