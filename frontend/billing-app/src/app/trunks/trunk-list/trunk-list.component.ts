import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrunkService } from '../trunk.service';
import { Trunk } from '../trunk';

@Component({
  selector: 'app-trunk-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trunk-list.component.html',
  styleUrls: ['./trunk-list.component.css']
})
export class TrunkListComponent implements OnInit {
  trunks: Trunk[] = [];

  constructor(private trunkSvc: TrunkService) {}

  ngOnInit(): void {
    this.trunkSvc.getTrunks().subscribe({
      next: (list: Trunk[]) => (this.trunks = list),
      error: (err: unknown) => console.error(err)
    });
  }
}
