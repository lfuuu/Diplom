import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  NonNullableFormBuilder,
  Validators,
  FormGroup,
  FormControl
} from '@angular/forms';
import { ClientService } from '../client.service';
import { Client } from '../clients';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.css']
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];

  /** форма только для создания */
  form!: FormGroup<{
    name: FormControl<string>;
    balance: FormControl<number>;
    isBlocked: FormControl<boolean>;
  }>;

  constructor(
    private clientSvc: ClientService,
    private fb: NonNullableFormBuilder
  ) {
    this.buildForm();
  }

  ngOnInit(): void {
    this.refresh();
  }

  /* ---------- helpers ---------- */

  private buildForm(): void {
    this.form = this.fb.group({
      name: this.fb.control('', Validators.required),
      balance: this.fb.control(0, Validators.required),
      isBlocked: this.fb.control(false, Validators.required)
    });
  }

  refresh(): void {
    this.clientSvc.getClients().subscribe({
      next: list => (this.clients = list),
      error: err => console.error(err)
    });
  }

  /* ---------- CREATE + DELETE ---------- */

  onSubmit(): void {
    if (this.form.invalid) return;

    const { name, balance, isBlocked } = this.form.getRawValue();
    this.clientSvc
      .createClient({ name, balance, isBlocked })
      .subscribe(() => {
        this.form.reset({ name: '', balance: 0, isBlocked: false });
        this.refresh();
      });
  }

  remove(id: number): void {
    if (!confirm('Удалить клиента?')) return;
    this.clientSvc.deleteClient(id).subscribe(() => this.refresh());
  }
}
