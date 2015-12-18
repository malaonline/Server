//
//  HomeViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let HomeViewCellReusedId = "HomeViewCellReusedId"

class HomeViewController: UICollectionViewController {
    
    // MARK: - Life Circle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Register Cell Class
        self.collectionView!.registerClass(UICollectionViewCell.self, forCellWithReuseIdentifier: HomeViewCellReusedId)

        // Setup UserInterface
        collectionView?.backgroundColor = UIColor.whiteColor()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Consturcted
    init() {
        super.init(collectionViewLayout: HomeViewFlowLayout())
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - DataSource
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 20
    }
    
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(HomeViewCellReusedId, forIndexPath: indexPath)
        
        // Configure the cell
        cell.backgroundColor = UIColor.lightGrayColor()
        
        return cell
    }
    
    
    // MARK: - Delegate
    override func collectionView(collectionView: UICollectionView, shouldHighlightItemAtIndexPath indexPath: NSIndexPath) -> Bool {
    return true
    }

    override func collectionView(collectionView: UICollectionView, shouldSelectItemAtIndexPath indexPath: NSIndexPath) -> Bool {
    return true
    }

}
