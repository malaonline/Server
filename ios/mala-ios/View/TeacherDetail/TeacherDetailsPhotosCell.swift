//
//  TeacherDetailsPhotosCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

class TeacherDetailsPhotosCell: MalaBaseCell {

    // MARK: - Property
    var photos: [String] = [] {
        didSet {
            // 老师详情中照片最少为3张
            if photos.count >= 3 {
                leftPhoto.kf_setImageWithURL(NSURL(string: photos[0])!,
                    placeholderImage: nil)
                centerPhoto.kf_setImageWithURL(NSURL(string: photos[1])!,
                    placeholderImage: nil)
                rightPhoto.kf_setImageWithURL(NSURL(string: photos[2])!,
                    placeholderImage: nil)
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var leftPhoto: UIImageView = UIImageView.placeHolder()
    private lazy var centerPhoto: UIImageView = UIImageView.placeHolder()
    private lazy var rightPhoto: UIImageView = UIImageView.placeHolder()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        content.addSubview(leftPhoto)
        content.addSubview(centerPhoto)
        content.addSubview(rightPhoto)

        // Autolayout
        leftPhoto.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_DetailPhotoHeight)
            make.width.equalTo(MalaLayout_DetailPhotoWidth)
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
        }
        centerPhoto.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(MalaLayout_DetailPhotoWidth)
            make.height.equalTo(self.leftPhoto.snp_height)
            make.top.equalTo(self.leftPhoto.snp_top)
            make.left.equalTo(self.leftPhoto.snp_right).offset(MalaLayout_Margin_5)
        }
        rightPhoto.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(self.centerPhoto.snp_height)
            make.top.equalTo(self.centerPhoto.snp_top)
            make.left.equalTo(self.centerPhoto.snp_right).offset(MalaLayout_Margin_5)
            make.right.equalTo(self.content.snp_right)
        }
    }
}
