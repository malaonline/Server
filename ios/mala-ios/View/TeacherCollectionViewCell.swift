//
//  TeacherCollectionViewCell.swift
//  mala-ios
//
//  Created by Erdi on 12/28/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

class TeacherCollectionViewCell: UICollectionViewCell {
    
    // MARK: - Variables
    var model: TeacherModel? {
        didSet{
            if model!.avatar != nil {
                imageView.kf_setImageWithURL(model!.avatar!,
                    placeholderImage: nil)
                //optionsInfo: [.Options(KingfisherOptions.ForceRefresh)])
            }
            
            priceLabel.text = String(format: "%d-%d¥/课时", model!.min_price, model!.max_price)
            
            nameLabel.text = model!.name
            nameLabel.sizeToFit()
            
            // Todo: convert grades and subject to what should be displayed
            gradeSubjectLabel.text = String(format: "年级:%d・科目:%d", model!.grades![0], model!.subject)
            gradeSubjectLabel.sizeToFit()
            print(gradeSubjectLabel.text)
            
            // Todo: convert tags to what should be displayed
            var tagsText: String = "风格:"
            for tag in model!.tags! {
                tagsText += String(tag) + "・"
            }
            print(tagsText)
            //tagsText.removeAtIndex(tagsText.endIndex)
            tagsLabel.text = tagsText
            tagsLabel.sizeToFit()
        }
    }
    
    // MARK: - Components
    private lazy var imageView: UIImageView = UIImageView()
    private lazy var priceLabel: UILabel = UILabel()
    private lazy var nameLabel: UILabel = UILabel()
    private lazy var gradeSubjectLabel: UILabel = UILabel()
    private lazy var tagsLabel: UILabel = UILabel()
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        self.model = nil
        super.init(frame: frame)
        
        setupUI()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Setup Cell UI
    private func setupUI() {
        
        imageView.addSubview(priceLabel)
        self.addSubview(imageView)
        self.addSubview(nameLabel)
        self.addSubview(gradeSubjectLabel)
        self.addSubview(tagsLabel)
        
        imageView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(imageView.snp_width)
        }
        
        priceLabel.backgroundColor = UIColor.blackColor()
        priceLabel.textColor = MalaAppearanceTextColor
        priceLabel.textAlignment = .Right
        priceLabel.alpha = 0.5
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(imageView.snp_left)
            make.right.equalTo(imageView.snp_right)
            make.bottom.equalTo(imageView.snp_bottom)
        }
        
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(imageView.snp_bottom)
        }
        
        gradeSubjectLabel.textColor = UIColor.redColor()
        gradeSubjectLabel.font = UIFont.systemFontOfSize(16)
        gradeSubjectLabel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(nameLabel.snp_bottom)
            make.right.equalTo(self.snp_right)
        }
        
        tagsLabel.textColor = UIColor.grayColor()
        tagsLabel.font = UIFont.systemFontOfSize(16)
        tagsLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(nameLabel.snp_bottom)
        }
    }
}
