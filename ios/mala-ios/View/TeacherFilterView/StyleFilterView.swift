//
//  StyleFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class StyleFilterView: ThemeTags {

    // MARK: - Property
    var tagsModel: [BaseObjectModel]? = nil {
        didSet {
            let array: [String]? = tagsModel?.map({ (model: BaseObjectModel) -> String in
                return model.name ?? ""
            })
            self.tags = array
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect, tags: [String]) {
        super.init(frame: frame, tags: tags)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
