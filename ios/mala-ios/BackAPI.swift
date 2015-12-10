//
//  BackAPI.swift
//  mala-ios
//
//  Created by Erdi on 12/7/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import Foundation

struct BackAPI {
  // Server domain
  private static let _domain = "https://dev.malalaoshi.com/"

  // API paths with version
  private static let _tokenAuth = "api/v1/token-auth/"
  //private static let _subjects = "api/v1/subjects/"
  private static let _grades = "api/v1/grades/"

  // API Urls
  static let tokenAuth = _domain + _tokenAuth
  //static let subjects = _domain + _subjects
  static let grades = _domain + _grades
}
