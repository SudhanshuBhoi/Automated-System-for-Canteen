//
//  MenuItemViewController.swift
//  Restaurant
//
//  Created by Sudhanshu Bhoi on 26/09/19.
//  Copyright © 2019 Sudhanshu Bhoi. All rights reserved.
//

import UIKit

class MenuItemViewController: UIViewController {
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var detailTextLabel: UILabel!
    @IBOutlet weak var addToOrderButton: UIButton!
    
    var menuItem: MenuItem!

    override func viewDidLoad() {
        super.viewDidLoad()

        addToOrderButton.layer.cornerRadius = 5.0
        updateUI()
    }
    
    func updateUI() {
        titleLabel.text = menuItem.name
        detailTextLabel.text = menuItem.detailText
        priceLabel.text = String(format: "Rs.%.2f", menuItem.price)
        MenuController.shared.fetchImage(from: menuItem.imageURL) { (image) in
            guard let image = image else { return }
            DispatchQueue.main.sync {
                self.imageView.image = image
            }
        }
    }
    
    @IBAction func addToOrderButtonTapped(_ sender: UIButton) {
        UIView.animate(withDuration: 0.3) {
            self.addToOrderButton.transform = CGAffineTransform(scaleX: 3.0, y: 3.0)
            self.addToOrderButton.transform = CGAffineTransform(scaleX: 1.0, y: 1.0)
        }
        
        MenuController.shared.order.menuItems.append(menuItem)
    }
    
    override func encodeRestorableState(with coder: NSCoder) {
        super.encodeRestorableState(with: coder)
        coder.encode(menuItem.id, forKey: "menuItemId")
    }
    
    override func decodeRestorableState(with coder: NSCoder) {
        super.decodeRestorableState(with: coder)
        
        let menuItemId = Int(coder.decodeInt32(forKey: "menuItemId"))
        menuItem = MenuController.shared.item(withId: menuItemId)!
        updateUI()
    }
}
