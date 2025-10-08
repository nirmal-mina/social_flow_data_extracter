import 'dart:convert';
import 'package:social_flow_data_extracter/exceptions/bad_url_exception.dart';
import 'package:social_flow_data_extracter/newpipeextractor_dart.dart';
import 'package:social_flow_data_extracter/utils/re_captcha.dart';
import 'package:social_flow_data_extracter/utils/string_checker.dart';

class CommentsExtractor {
  static Future<List<YoutubeComment>> getComments(String videoUrl) async {
    if (StringChecker.hasWhiteSpace(videoUrl)) {
      throw const BadUrlException("Url is null or contains white space");
    }
    List<YoutubeComment> comments = [];
    Future<dynamic> task() => NewPipeExtractorDart.extractorChannel
        .invokeMethod('getComments', {"videoUrl": videoUrl});
    var info = await task();
    // Check if we got reCaptcha needed response
    info = await ReCaptchaPage.checkInfo(info, task);
    info.forEach((key, map) {
      comments.add(YoutubeComment(
          author: map['author'],
          commentText: map['commentText'],
          uploadDate: map['uploadDate'],
          uploaderAvatars:
              List<String>.from(jsonDecode(map['uploaderAvatars'])),
          uploaderUrl: map['uploaderUrl'],
          commentId: map['commentId'],
          likeCount: int.parse(map['likeCount']),
          hearted: map['hearted'].toLowerCase() == 'true',
          pinned: map['pinned'].toLowerCase() == 'true'));
    });
    return comments;
  }
}
