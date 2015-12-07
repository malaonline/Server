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

  // API paths
  private static let _tokenAuth_v1 = "api/v1/token-auth/"

  // API URL(s)
  static let tokenAuth_v1 = _domain + _tokenAuth_v1
}
