//
//  TeacherDetailsPhotosCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsPhotosCell: TeacherDetailsBaseCell {

    // MARK: - Components
    var photos: [String] {
        didSet {
            
        }
    }
    var leftPhoto: UIImageView = UIImageView()
    var centerPhoto: UIImageView = UIImageView()
    var rightPhoto: UIImageView = UIImageView()
    
    
    // MARK: - Life Cycle
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        self.photos = []
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        
        leftPhoto.backgroundColor = UIColor.blueColor()
        centerPhoto.backgroundColor = UIColor.orangeColor()
        rightPhoto.backgroundColor = UIColor.redColor()
        
        // SubViews
        content.addSubview(leftPhoto)
        content.addSubview(centerPhoto)
        content.addSubview(rightPhoto)
        
        // Autolayout
        leftPhoto.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.height.equalTo(MalaLayout_DetailPhotoHeight)
        }
        centerPhoto.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.leftPhoto.snp_top)
            make.left.equalTo(self.leftPhoto.snp_right).offset(MalaLayout_Margin_5)
            make.width.equalTo(self.leftPhoto.snp_width)
            make.height.equalTo(self.leftPhoto.snp_height)
        }
        rightPhoto.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.centerPhoto.snp_top)
            make.left.equalTo(self.centerPhoto.snp_right).offset(MalaLayout_Margin_5)
            make.width.equalTo(self.centerPhoto.snp_width)
            make.height.equalTo(self.centerPhoto.snp_height)
            make.right.equalTo(self.content.snp_right)
        }
        
    }

}
