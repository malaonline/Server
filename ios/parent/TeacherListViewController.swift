//
//  TeacherListViewController.swift
//  mala-ios
//
//  Created by Liang Sun on 11/10/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class TeacherListViewController: UIViewController {

  // static data for test
  let image = UIImage(data: NSData(contentsOfURL: NSURL(string: "http://img1.touxiang.cn/uploads/20120419/19-020539_131.jpg")!)!)

  override func viewDidLoad() {
    super.viewDidLoad()
    // todo: init the data source
  }

  func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int
  {
    return 100;
  }

  func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell
  {
    let cell: TeacherCollectionCell = collectionView.dequeueReusableCellWithReuseIdentifier("TeacherCollectionCell", forIndexPath: indexPath) as! TeacherCollectionCell
    cell.imageView.image = image

    return cell;
  }

  func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath)
  {
    print("select cell: \(indexPath.row)")
  }
}